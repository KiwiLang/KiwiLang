package com.buttersus.frontend.syntax

import com.buttersus.frontend.lexical.*
import kotlin.math.*
import kotlin.reflect.KFunction0

/**
 * Base class for parsers.
 * It's used to separate grammar rules into different classes.
 *
 * @param parser The main parser, that stores the context.
 */
@Suppress("MemberVisibilityCanBePrivate")
internal abstract class ParserBase(
    private val parser: Parser
) {
    // LOGIC PARTS
    // ===========

    protected val logicPackage: LogicPackage
        inline get() = parser.logicPackage
    protected val logicExpression: LogicExpression
        inline get() = parser.logicExpression
    protected val logicDeclaration: LogicDeclaration
        inline get() = parser.logicDeclaration
    protected val logicClass: LogicClass
        inline get() = parser.logicClass
    protected val logicStatement: LogicStatement
        inline get() = parser.logicStatement
    protected val logicModifier: LogicModifier
        inline get() = parser.logicModifier
    protected val logicFunction: LogicFunction
        inline get() = parser.logicFunction

    // PARSER CONTEXT
    // ==============

    @Suppress("unused")
    protected val lexer: Lexer
        inline get() = parser.lexer
    private val lexerStream: Iterator<TokenInfo>
        inline get() = parser.lexerStream
    private val source: String
        inline get() = parser.source
    private val filePath: String
        inline get() = parser.filePath
    private val memories: MutableMap<Int, MutableMap<() -> TokenAST?, Pair<TokenAST?, Int>>>
        inline get() = parser.memories
    private val lexemes: ArrayList<TokenInfo>
        inline get() = parser.lexemes
    private var index: Int
        inline get() = parser.index
        inline set(value) {
            parser.index = value
        }

    // TOKEN METHODS
    // =============

    /**
     * Returns current token index.
     */
    protected fun mark(): Int = index

    /**
     * Resets the token index to the given value.
     */
    protected fun reset(index: Int) {
        this.index = index
    }

    /**
     * Returns the next token and consumes it.
     */
    protected fun next() = peek().also { index++ }

    /**
     * Returns the next token without consuming it.
     */
    protected fun peek(): TokenAST.TokenWrapper {
        while (lexemes.size <= index)
            lexemes.add(lexerStream.next())
        return TokenAST.TokenWrapper(lexemes[index])
    }

    /**
     * Raises a syntax error.
     * @param message The error message.
     * @param expected The expected token.
     */
    @Suppress("SameParameterValue")
    protected fun raise(message: String, expected: String): Nothing {
        val token = peek().token
        val lines = source.lines()
        val offset = floor(log10(token.line + 1.0)).toInt() + 1
        val size = token.lexeme.length
        throw SyntaxError(
            """
error: expected `$expected`, found `${token.lexeme}`
--> $filePath:${token.line + 1}:${token.column + 1}
|
| ${
                arrayOf(3, 2, 1, 0).mapNotNull {
                    if (token.line - it >= 0) "${
                        (token.line - it + 1).toString().padStart(offset)
                    }. ${
                        lines[token.line - it]
                    }" else null
                }.joinToString("\n| ")
            }
| ${" ".repeat(token.column + offset + 1)} ${"^".repeat(size)} $message
|
| ------ syntax error
error: could not compile `$filePath` due to previous error
            """.trimIndent()
        )
    }

    // MEMOIZATION METHODS
    // ===================

    /**
     * Memoize a function without left recursion checking.
     * @param function The function to be memoized.
     * @param code The code to be memoized.
     */
    @Suppress("SameParameterValue")
    protected fun memoize(
        function: KFunction0<TokenAST?>,
        code: () -> TokenAST?
    ): TokenAST? {
        val position = mark()
        val memory = memories.getOrPut(position) { mutableMapOf() }
        memory[function]?.also {
            reset(it.second)
            return it.first
        }
        return code().also {
            memory[function] = it to mark()
        }
    }

    /**
     * Memoize a function with left recursion checking.
     * @param function The function to be memoized.
     * @param code The code to be memoized.
     */
    @Suppress("SameParameterValue")
    protected fun memoizeLR(
        function: KFunction0<TokenAST?>,
        code: () -> TokenAST?
    ): TokenAST? {
        val position = mark()
        val memory = memories.getOrPut(position) { mutableMapOf() }
        memory[function]?.also {
            reset(it.second)
            return it.first
        }
        var lastResult: TokenAST? = null
        var lastPosition = position
        memory[function] = null to lastPosition
        while (true) {
            reset(position)
            val result = code()
            val newPosition = mark()
            if (newPosition <= lastPosition) break
            lastResult = result
            lastPosition = newPosition
            memory[function] = lastResult to lastPosition
        }
        return lastResult.also {
            reset(lastPosition)
        }
    }

    // PARSE METHODS
    // =============

    /**
     * Returns parsed token or null without consuming it.
     *
     * > e.g: `?=LOOKAHEAD`
     *
     * @param code The code to be parsed.
     * @return The parsed token or null.
     */
    @Suppress("SameParameterValue")
    protected fun lookahead(code: () -> TokenAST?): TokenAST? {
        val mark = mark()
        val result = code()
        return result.also { reset(mark) }
    }

    /**
     * Parses all the given tokens.
     *
     * > e.g: `{ OPTIONAL1 OPTIONAL2 }?`
     *
     * @param codes The tokens to be parsed.
     * @return Null if any of them fails.
     */
    @Suppress("SameParameterValue")
    protected fun optional(vararg codes: () -> TokenAST?): Unit? {
        val mark = mark()
        codes.map { it() ?: return null.also { reset(mark) } }.toTypedArray()
        return Unit
    }

    /**
     * Parses given token type
     *
     * > e.g: `<TOKEN_TYPE>`
     *
     * @param type The token type to be parsed.
     * @return The parsed token or null.
     */
    @Suppress("SameParameterValue")
    protected fun match(type: TokenType): TokenAST? {
        val token = peek().token
        if (token.type == TokenType.KEYWORD && type == TokenType.IDENTIFIER)
            raise(
                "wrong identifier name (reserved keyword)",
                "identifier"
            )
        return if (token.type == type) next() else null
    }

    /**
     * Parses a token with the given lexeme.
     *
     * > e.g: `"LEXEME"`
     *
     * @param lexeme The lexeme to be parsed.
     * @return The parsed token or null.
     */
    @Suppress("SameParameterValue")
    protected fun match(lexeme: String) =
        if (peek().token.lexeme == lexeme) next() else null

    /**
     * Parses given tokens until one of them is found.
     *
     * > e.g: `{ ALTERNATIVE1 | ALTERNATIVE2 }`
     *
     * @param codes The tokens to be parsed.
     * @return The parsed token or null.
     */
    @Suppress("SameParameterValue")
    protected fun alternativeSingle(
        vararg codes: () -> TokenAST?
    ): TokenAST? {
        return codes.map { it() }.firstOrNull { it != null }
    }

    /**
     * Parses given token sequences until one of them is found.
     *
     * > e.g: `{ ALTERNATIVE1 ... | ALTERNATIVE2 ... }`
     *
     * @param codes The token sequences to be parsed.
     * @return Null if all of them fail.
     */
    @Suppress("SameParameterValue")
    protected fun alternativeMulti(
        vararg codes: () -> Unit?
    ): Unit? {
        val mark = mark()
        codes.forEach {
            it()?.also { return Unit } ?: reset(mark)
        }; return null
    }

    /**
     * Parser given token value separated by given token separator.
     *
     * > e.g: `SEPARATOR:VALUE+`
     *
     * @param value The token value to be parsed.
     * @param separator The token separator to separate values.
     * @param from The minimum number of values to be parsed.
     * @param to The maximum number of values to be parsed.
     * @return The parsed token values or null if the number of values is not in the range.
     */
    @Suppress("SameParameterValue")
    protected fun separator(
        value: () -> TokenAST?, separator: () -> TokenAST?,
        from: Int = 1, to: Int = Int.MAX_VALUE
    ): TokenAST.List? {
        val list = TokenAST.List()
        var mark = mark()
        value()?.let { list.add(it) } ?: return (if (list.size < from) null.also {
            reset(mark)
        } else list)
        while (list.size < to) {
            mark = mark()
            separator() ?: run {
                reset(mark)
                return (if (list.size < from) null.also {
                    reset(mark)
                } else list)
            }; value()?.let { list.add(it) } ?: run {
                reset(mark)
                return (if (list.size < from) null.also {
                    reset(mark)
                } else list)
            }
        }
        return list
    }

    /**
     * Parses given token as much as possible.
     *
     * > e.g: `VALUE+`
     *
     * @param from The minimum number of values to be parsed.
     * @param to The maximum number of values to be parsed.
     * @param value The token value to be parsed.
     * @return The parsed token list or null if the number of values is not in the range.
     */
    @Suppress("SameParameterValue")
    protected fun list(from: Int = 1, to: Int = Int.MAX_VALUE, value: () -> TokenAST?): TokenAST.List? {
        val list = TokenAST.List()
        while (list.size < to) {
            val mark = mark()
            value()?.let { list.add(it) } ?: run {
                reset(mark)
                if (list.size < from) return null
                return list
            }
        }
        return list
    }

    /**
     * Replaces given token with given token if it is void.
     *
     * > e.g: `TOKEN[=?REPLACE]`
     *
     * @param tokenAST The token to be checked.
     * @param replaceWith The token to replace with.
     * @return The token or replaced token.
     */
    protected fun voidCheck(
        tokenAST: TokenAST,
        replaceWith: TokenAST
    ) = if (tokenAST is TokenAST.Void) replaceWith else tokenAST
}