package com.kiwilang.frontend.syntax

internal class LogicFunction(
    parser: Parser
) : ParserBase(parser) {

    fun functionValueParameters(): TokenAST? = memoize(::functionValueParameters) {
        val mark = mark()

        // ='(' .',':functionValueParameter* ','? !')' -> "missing closing bracket"
        fun case0(): TokenAST? {
            match("(") ?: return null
            val itself = separator(
                { functionValueParameter() },
                { match(",") }
            ) ?: TokenAST.List()
            match(",")
            match(")") ?: raise("missing closing bracket", "')'")
            return itself
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun functionValueParameter(): TokenAST? = memoize(::functionValueParameter) {
        val mark = mark()

        // .parameterModifiers?[List] .{2}parameter {
        // '=' .!expression -> "missing default value in parameter"
        // }? => FunctionValueParameter(modifiers, name, type, defaultValue)
        fun case0(): TokenAST? {
            val modifiers = logicClass.parameterModifiers() ?: TokenAST.List()
            val parameter = logicClass.parameter() as TokenAST.List? ?: return null
            var defaultValue: TokenAST = TokenAST.Void
            optional(
                { match("=") },
                { logicExpression.expression()?.also { defaultValue = it } }
            ) ?: run {
                defaultValue = TokenAST.Void
            }
            return TokenAST.FunctionValueParameter(modifiers, parameter[0], parameter[1], defaultValue)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    fun functionBody(): TokenAST? = memoize(::functionBody) {
        val mark = mark()

        // ={block | '=' =expression}
        fun case0(): TokenAST? {
            var itself: TokenAST = TokenAST.Void
            alternativeMulti(
                {
                    val first = logicStatement.block() ?: return@alternativeMulti null
                    itself = first
                    Unit
                },
                {
                    match("=") ?: return@alternativeMulti null
                    val first = logicExpression.expression() ?: return@alternativeMulti null
                    itself = first
                    Unit
                }
            ) ?: return null
            return itself
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }
}