package com.buttersus.frontend.lexical

import kotlin.math.log10
import kotlin.math.ceil
import kotlin.math.floor

class Lexer {
    private lateinit var source: String
    internal var index = 0
    internal var line = 0
    internal var column = 0
    private lateinit var filePath: String
    private val keywords = setOf(
        "true", "false", "none",
        "package", "import", "fun",
        "if", "else", "for", "while",
        "return", "break", "continue",
        "pass", "class", "in",
    )

    /**
     * Loads the source code to be lexed.
     */
    fun load(source: String, filePath: String = "terminal"): Lexer = this.also {
        this.filePath = filePath
        this.source = source
        index = 0
        line = 0
        column = 0
    }

    private val functions = mapOf(
        TokenType.PACKAGE_NAME to {
            Regex("[\\p{L}0-9_]+(\\.[\\p{L}0-9_]+)*\\b").matchAt(source, index)?.let { match ->
                TokenType.PACKAGE_NAME to match.value
            } ?: raise("wrong package name", "PACKAGE_NAME")
        },
        TokenType.IMPORT_PACKAGE_NAME to {
            Regex("[\\p{L}0-9_]+(\\.[\\p{L}0-9_]+)*(\\.\\*)?\\b").matchAt(source, index)?.let { match ->
                TokenType.IMPORT_PACKAGE_NAME to match.value
            } ?: raise("wrong import package name", "IMPORT_PACKAGE_NAME")
        }
    )

    private fun raise(message: String, type: String): Nothing {
        lateinit var found: String
        val offset = floor(log10(index + 1.0)).toInt() + 1
        var size = 0
        Regex("[\"'][^\n]*").matchAt(source, index)?.let { match ->
            found = match.value
            size = match.value.length
        } ?: Regex("\\S+").matchAt(source, index)?.let {
            size = it.value.length
            found = "found `${it.value}`"
        } ?: run {
            found = "nothing found"
        }
        // Here we don't add +1 to line, because index is located right after the token.
        throw LexicalError(
            """
error: wrong token of type `$type`, $found
--> $filePath:${line + 1}:${column + 1}
|
| ${
                arrayOf(3, 2, 1, 0).mapNotNull {
                    if (line - it >= 0) "${
                        (line - it + 1).toString().padStart(ceil(log10(line + 1f)).toInt())
                    }. ${
                        source.lines()[line - it]
                    }" else null
                }.joinToString("\n| ")
            }
| ${" ".repeat(column + offset)} ${"^".repeat(size)} $message
|
| ------- lexical error
error: could not compile `$filePath` due to previous error
            """.trimIndent()
        )
    }

    private var isEnable = false
    private lateinit var enabledFunction: () -> Pair<TokenType, String>

    /**
     * Turn on the rule for the specified token type.
     */
    fun enable(type: TokenType) {
        isEnable = true
        enabledFunction = functions[type]!!
    }

    /**
     * Returns the iterator of spaces and newlines in the source code.
     */
    private fun spaces(): Iterator<TokenInfo> = iterator {
        Regex("[ \t]+").matchAt(source, index)?.let { match ->
            index += match.value.length
            column += match.value.length
        }
        var isFirst = true
        while (index < source.length) {
            Regex("\r?\n").matchAt(source, index)?.let { match ->
                if (isFirst) {
                    val started = Triple(line, column, index)
                    index += match.value.length
                    line++
                    column = 0
                    yield(TokenInfo(TokenType.NEWLINE, match.value, this@Lexer, started))
                    isFirst = false
                } else {
                    index += match.value.length
                    line++
                    column = 0
                }
            } ?: break
            Regex("[ \t]+").matchAt(source, index)?.let { match ->
                index += match.value.length
                column += match.value.length
            }
        }
    }

    /**
     * Returns the iterator of tokens in the source code.
     */
    private fun comments(): Iterator<TokenInfo> = iterator {
        Regex("#[^\n]*\n").matchAt(source, index)?.let { match ->
            index += match.value.length
            column += match.value.length
        }
    }

    /**
     * Returns the iterator of literals in the source code.
     */
    private fun literals(): Pair<TokenType, String>? {
        if (isEnable) {
            isEnable = false
            return enabledFunction()
        }
        Regex("0x([0-9a-fA-F]_?)*[0-9a-fA-F]\\b").matchAt(source, index)?.let { match ->
            return TokenType.NUMBER to match.value
        }
        Regex("0c([0-7]_?)*[0-7]\\b").matchAt(source, index)?.let { match ->
            return TokenType.NUMBER to match.value
        }
        Regex("0b([01]_?)*[01]\\b").matchAt(source, index)?.let { match ->
            return TokenType.NUMBER to match.value
        }
        Regex("(([0-9]_?)*[0-9])?\\.(([0-9]_?)*[0-9])?([eE][+-]?(([0-9]_?)*[0-9]))?\\b").matchAt(source, index)
            ?.let { match ->
                return TokenType.NUMBER to match.value
            }
        Regex("([0-9]_?)*[0-9]\\b").matchAt(source, index)?.let { match ->
            return TokenType.NUMBER to match.value
        }
        Regex("\"{3}([^\"]|\\\\\"|\"(?!\"{2}))*\"{3}").matchAt(source, index)?.let { match ->
            return TokenType.DOCSTRING to match.value
        }
        Regex("\"([^\"\\\\]|\\\\.)*\"").matchAt(source, index)?.let { match ->
            if (match.value.contains("\n")) raise("string literal must be on one line", "STRING")
            return TokenType.STRING to match.value
        }
        Regex("\'{3}([^\']|\\\\\'|\'(?!\'{2}))*\'{3}").matchAt(source, index)?.let { match ->
            return TokenType.DOCSTRING to match.value
        }
        Regex("'([^'\\\\]|\\\\.)*'").matchAt(source, index)?.let { match ->
            if (match.value.contains("\n")) raise("string literal must be on one line", "STRING")
            return TokenType.STRING to match.value
        }
        Regex("[\\p{L}_][\\p{L}0-9_]*\\b").matchAt(source, index)?.let { match ->
            if (match.value in keywords) {
                return TokenType.KEYWORD to match.value
            }
            return TokenType.IDENTIFIER to match.value
        }
        Regex("\\$[\\p{L}_][\\p{L}0-9_]*\\b").matchAt(source, index)?.let { match ->
            if (match.value in keywords) {
                return TokenType.KEYWORD to match.value
            }
            return TokenType.CONST_IDENTIFIER to match.value
        }
        return null
    }

    /**
     * Returns the iterator of characters in the source code.
     */
    private fun characters(): Iterator<TokenInfo> = iterator {
        while (true) {
            Regex("\\?:|->|<-|&{2}|\\^{2}|\\|{2}|/{2}|\\*{2}|" +
                    "(\\*{2}|/{2}|[+\\-*/%<>=!(\\[{])=?|\\.\\.|[:@,.?;]").matchAt(
                source, index
            )?.let { match ->
                val started = Triple(line, column, index)
                index += match.value.length
                column += match.value.length
                yield(TokenInfo(TokenType.RESERVED, match.value, this@Lexer, started))
                spaces().forEach { _ -> }
            } ?: Regex("\\s*[])}]").matchAt(source, index)?.let { match ->
                val started = Triple(line, column, index)
                line += match.value.count("\n"::contains)
                if (line != started.first)
                    column = match.value.length - match.value.lastIndexOf("\n") - 1
                else
                    column += match.value.length
                index += match.value.length
                yield(TokenInfo(TokenType.RESERVED, match.value.last().toString(), this@Lexer, started))
            } ?: break
        }
    }

    /**
     * Returns the iterator of tokens in the source code.
     */
    fun tokens(): Iterator<TokenInfo> = iterator {
        while (index < source.length) {
            val indexBefore = index
            yieldAll(spaces())
            yieldAll(comments())
            literals()?.let {
                val started = Triple(line, column, index)
                line += it.second.count("\n"::contains)
                column += it.second.length - it.second.lastIndexOf("\n") - 1
                index += it.second.length
                yield(TokenInfo(it.first, it.second, this@Lexer, started))
            }
            yieldAll(characters())
            if (indexBefore == index) raise("unknown token", "???")
        }
        yield(TokenInfo(TokenType.NEWLINE, this@Lexer))
        yield(TokenInfo(TokenType.EOF, this@Lexer))
    }
}