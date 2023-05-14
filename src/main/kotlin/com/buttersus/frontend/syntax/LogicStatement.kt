package com.buttersus.frontend.syntax

internal class LogicStatement(
    parser: Parser
) : ParserBase(parser) {
    fun block(): TokenAST? = memoize(::block) {
        val mark = mark()

        // '{' .statements !'}' -> "missing closing brace"
        fun case0(): TokenAST? {
            match("{") ?: return null
            val itself = logicStatement.statements() ?: return null
            match("}") ?: raise("missing closing brace", "'}'")
            return itself
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun statements(): TokenAST? = memoize(::statements) {
        val mark = mark()

        // =.semi:statement* !{ semi | ?!'}' } -> "expected newline or semicolon"
        fun case0(): TokenAST? {
            val itself = separator(
                { logicStatement.statement() },
                { logicExpression.semi() }
            ) ?: TokenAST.List()
            alternativeSingle(
                { logicExpression.semi() },
                { lookahead { match("}") } }
            ) ?: raise("expected newline or semicolon", "semi")
            return itself
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun statement(): TokenAST? = memoize(::statement) {
        val mark = mark()

        // ={declaration | expression}
        alternativeSingle(
            { logicDeclaration.declaration() },
            { logicExpression.expression() }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }
}