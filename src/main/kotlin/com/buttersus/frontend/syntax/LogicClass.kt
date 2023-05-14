package com.buttersus.frontend.syntax

internal class LogicClass(
    parser: Parser
) : ParserBase(parser) {

    fun parameterModifiers(): TokenAST? = memoize(::parameterModifiers) {
        val mark = mark()

        // TODO: Implement

        return@memoize null
    }

    fun modifiers(): TokenAST? = memoize(::modifiers) {
        val mark = mark()

        // =modifier+
        list { modifier() }?.also { return@memoize it }

        return@memoize null
    }

    private fun modifier(): TokenAST? = memoize(::modifier) {
        val mark = mark()

        // TODO: Implement

        return@memoize null
    }

    fun parameter(): TokenAST? = memoize(::parameter) {
        val mark = mark()

        // TODO: Implement

        return@memoize null
    }
}