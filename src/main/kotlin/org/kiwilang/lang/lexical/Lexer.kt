package org.kiwilang.lang.lexical

import org.kiwilang.lang.Compiler
import java.nio.file.Path

/**
 * This class is used to convert a source string into a stream of tokens.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Lexer internal constructor(
    // Top-level components
    internal var compiler: Compiler
) {
    // PROPERTIES
    // ==========

    // Source
    internal lateinit var source: String
    internal var file: Path? = null

    // Index
    private var _endIndex = 0
    internal var endIndex: Int
        get() = _endIndex
        set(value) {
            startIndex = _endIndex
            _endIndex = value
        }
    internal var startIndex = 0

    // Line
    private var _endLine = 1
    internal var endLine: Int
        get() = _endLine
        set(value) {
            startLine = _endLine
            _endLine = value
        }
    internal var startLine = 1

    // Column
    private var _endColumn = 0
    internal var endColumn: Int
        get() = _endColumn
        set(value) {
            startColumn = _endColumn + 1
            _endColumn = value
        }
    internal var startColumn = 1

    // METHODS
    // =======

    /**
     * Loads the source string to be tokenized.
     */
    fun load(source: String): Lexer {
        this.source = source
        return this
    }

    /**
     * Loads the source string to be tokenized.
     */
    fun load(file: Path): Lexer {
        this.file = file
        load(file.toFile().readText())
        return this
    }

    /**
     * Consumes the match and updates the end index, line, and column.
     */
    internal fun consume(match: MatchResult) {
        endIndex = match.range.last + 1
        endLine += source.subSequence(startIndex, endIndex).count { it == '\n' }
        val lastLineBreak = source.lastIndexOf('\n', match.range.last)
        endColumn = if (lastLineBreak == -1) endColumn + match.value.length else endIndex - lastLineBreak - 1
    }

    /**
     * Looks for whitespace characters and newlines.
     */
    private fun isWhitespace() = iterator<TokenInfo> {
        Regex("\\s+").matchAt(source, endIndex)?.also { match ->
            consume(match)
        }
    }

    /**
     * Looks for literals: numbers, strings, etc.
     */
    private fun isLiteral() = iterator<TokenInfo> {
//        yieldAll(TokenInfo.TokenNumber.consume(this@Lexer))
//        TokenType.STRING.regex.matchAt(source, endIndex)?.also { match ->
//            yield(TokenInfo.TokenString(this@Lexer, match))
//        }
//        TokenType.DOCSTRING.regex.matchAt(source, endIndex)?.also { match ->
//            yield(TokenInfo.TokenDocString(this@Lexer, match))
//        }
//        TokenType.IDENTIFIER.regex.matchAt(source, endIndex)?.also { match ->
//            yield(TokenInfo.TokenIdentifier(this@Lexer, match))
//        }
    }

    /**
     * Tokenizes the source string.
     */
    fun tokenize() = iterator {
        while (endIndex < source.length) {
            val savedIndex = endIndex
            yieldAll(isWhitespace())
            yieldAll(isLiteral())
            if (endIndex == savedIndex) {
                consume(
                    Regex(".").matchAt(source, endIndex)
                        ?: throw LexicalException.EndOfFileException(
                            this@Lexer
                        )
                )
                throw LexicalException.UnexpectedCharacterException(
                    this@Lexer, source[startIndex]
                )
            }
        }
        yield(TokenInfo.TokenEndOfFile(this@Lexer))
    }
}