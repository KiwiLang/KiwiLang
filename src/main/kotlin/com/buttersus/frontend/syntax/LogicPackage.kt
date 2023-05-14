package com.buttersus.frontend.syntax

import com.buttersus.frontend.lexical.TokenType

internal class LogicPackage(
    parser: Parser
) : ParserBase(parser) {

    fun kiwiFile(): TokenAST {
        val mark = mark()

        // =.packageHeader .importList .topLevelObject* !<EOF> -> "unexpected token occurred"
        // <NEWLINE>? => KiwiFile(packageName, importList, body)
        fun case0(): TokenAST? {
            val packageLocation = logicPackage.packageHeader() ?: return null
            val importList = logicPackage.importList() ?: return null
            val body = list { logicPackage.topLevelObject() } ?: TokenAST.List()
            match(TokenType.NEWLINE)
            match(TokenType.EOF) ?: raise("unexpected token occurred", "<EOF>")
            return TokenAST.KiwiFile(packageLocation, importList, body)
        }

        case0()?.let { return it }
        reset(mark)

        raise("Grammar mismatch", "KiwiFile")
    }

    private fun packageHeader(): TokenAST? = memoize(::packageHeader) {
        // ={'package' .identifier !semi -> "expected newline or semicolon"}?
        var itself: TokenAST = TokenAST.Void
        optional(
            { match("package") },
            { logicExpression.identifier()?.also { itself = it } },
            { logicExpression.semi() ?: raise("expected newline or semicolon", "semi") }
        ) ?: run {
            itself = TokenAST.Void
        }
        return@memoize itself
    }

    private fun importList(): TokenAST? = memoize(::importList) {
        // =importHeader*
        return@memoize list { logicPackage.importHeader() } ?: TokenAST.List()
    }

    private fun importHeader(): TokenAST? = memoize(::importHeader) {
        val mark = mark()

        // 'import' .!identifier -> "missing identifier"
        // { .2{'.' '*'}[={TokenAST.Boolean.True}] | .1importAlias }?
        // !semi -> "expected newline or semicolon"
        // => Import(packageLocation, alias=1, isAll=2[=?{TokenAST.Boolean.False}])
        fun case0(): TokenAST? {
            match("import") ?: return null
            val packageLocation = logicExpression.identifier() ?: raise("missing identifier", "identifier")
            var isAll: TokenAST = TokenAST.Void
            var alias: TokenAST = TokenAST.Void
            alternativeMulti(
                {
                    match(".") ?: return@alternativeMulti null
                    match("*") ?: return@alternativeMulti null
                    isAll = TokenAST.Boolean.True
                    Unit
                },
                {
                    val first = logicPackage.importAlias() ?: return@alternativeMulti null
                    alias = first
                    Unit
                }
            )
            logicExpression.semi() ?: raise("expected newline or semicolon", "semi")
            return TokenAST.Import(packageLocation, alias, voidCheck(isAll, TokenAST.Boolean.False))
        }

        case0()?.let { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun importAlias(): TokenAST? = memoize(::importAlias) {
        val mark = mark()

        // ='as' .!simpleIdentifier -> "Expected identifier"
        fun case0(): TokenAST? {
            match("as") ?: return null
            val itself = logicExpression.simpleIdentifier()
                ?: raise("Expected identifier", "simpleIdentifier")
            return itself
        }

        case0()?.let { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun topLevelObject(): TokenAST? = memoize(::topLevelObject) {
        val mark = mark()

        // =.declaration !semi -> "expected newline or semicolon"
        fun case0(): TokenAST? {
            val itself = logicDeclaration.declaration() ?: return null
            logicExpression.semi() ?: raise("expected newline or semicolon", "semi")
            return itself
        }

        case0()?.let { return@memoize it }
        reset(mark)

        return@memoize null
    }
}