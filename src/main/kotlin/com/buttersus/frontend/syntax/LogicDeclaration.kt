package com.buttersus.frontend.syntax

internal class LogicDeclaration(
    parser: Parser
) : ParserBase(parser) {

    fun declaration(): TokenAST? = memoize(::declaration) {
        val mark = mark()

        // ={classDeclaration | functionDeclaration | variableDeclaration
        // | propertyDeclaration | typeAlias }
        alternativeSingle(
            { classDeclaration() },
            { functionDeclaration() },
            { variableDeclaration() },
            { propertyDeclaration() },
            { typeAlias() }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun classDeclaration(): TokenAST? = memoize(::classDeclaration) {
        val mark = mark()

        // TODO: Implement

        return@memoize null
    }

    private fun functionDeclaration(): TokenAST? = memoize(::functionDeclaration) {
        val mark = mark()

        // .modifiers? 'fun' {.receiverType '.'}? .simpleIdentifier .functionValueParameters
        // {'->' .type}? .!functionBody -> "missing function body"
        // => FunctionDeclaration(modifiers, receiverType, name, parameters, returnType, body)
        fun case0(): TokenAST? {
            val modifiers = logicClass.modifiers() ?: TokenAST.Void
            match("fun") ?: return null
            var receiverType: TokenAST = TokenAST.Void
            optional(
                { logicExpression.receiverType()?.also { receiverType = it } },
                { match(".") }
            ) ?: run {
                receiverType = TokenAST.Void
            }
            val name = logicExpression.simpleIdentifier() ?: return null
            val parameters = logicFunction.functionValueParameters() ?: return null
            var returnType: TokenAST = TokenAST.Void
            optional(
                { match("->") },
                { logicExpression.type()?.also { returnType = it } }
            ) ?: run {
                returnType = TokenAST.Void
            }
            val body = logicFunction.functionBody() ?: raise("missing function body", "functionBody")
            return TokenAST.FunctionDeclaration(modifiers, receiverType, name, parameters, returnType, body)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun variableDeclaration(): TokenAST? = memoize(::variableDeclaration) {
        val mark = mark()

        // TODO: Implement

        return@memoize null
    }

    private fun propertyDeclaration(): TokenAST? = memoize(::propertyDeclaration) {
        val mark = mark()

        // TODO: Implement

        return@memoize null
    }

    private fun typeAlias(): TokenAST? = memoize(::typeAlias) {
        val mark = mark()

        // TODO: Implement

        return@memoize null
    }
}