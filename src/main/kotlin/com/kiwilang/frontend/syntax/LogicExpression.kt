package com.kiwilang.frontend.syntax

import com.kiwilang.frontend.lexical.TokenType

internal class LogicExpression(
    parser: Parser
) : ParserBase(parser) {

    fun semi(): TokenAST? = memoize(::semi) {
        val mark = mark()

        // ={<NEWLINE> | =';' !?!';' -> "do not use multiple semicolons"}
        alternativeSingle(
            { match(TokenType.NEWLINE) },
            {
                val itself = match(";")
                lookahead { match(";") }?.also {
                    raise("do not use multiple semicolons", "?!;")
                }
                itself
            }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    fun expression(): TokenAST? = memoize(::expression) {
        val mark = mark()

        // TODO: Implement

        return@memoize null
    }

    fun identifier(): TokenAST? = memoize(::identifier) {
        val mark = mark()

        // ='.':simpleIdentifier+
        separator(
            { simpleIdentifier() },
            { match(".") }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    fun simpleIdentifier(): TokenAST? = memoize(::simpleIdentifier) {
        val mark = mark()

        // =<IDENTIFIER>
        match(TokenType.IDENTIFIER)?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    fun type(): TokenAST? = memoize(::type) {
        val mark = mark()

        // typeModifiers? {functionType | parenthesizedType | typeReference }
        // => Type(modifiers, type)


        return@memoize null
    }

    fun receiverType(): TokenAST? = memoize(::receiverType) {
        val mark = mark()

        // TODO: Implement

        return@memoize null
    }
}