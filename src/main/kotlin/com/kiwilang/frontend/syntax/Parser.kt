package com.kiwilang.frontend.syntax

import com.buttersus.frontend.lexical.*
import com.kiwilang.frontend.lexical.Lexer
import com.kiwilang.frontend.lexical.TokenInfo

/**
 * The main parser class.
 * It's used to control the context of the parsing process.
 */
open class Parser(
    lexer: Lexer? = null
) {
    // LOGIC PARTS
    // ===========

    internal val logicPackage
            by lazy { LogicPackage(this) }
    internal val logicExpression
            by lazy { LogicExpression(this) }
    internal val logicDeclaration
            by lazy { LogicDeclaration(this) }
    internal val logicClass
            by lazy { LogicClass(this) }
    internal val logicStatement
            by lazy { LogicStatement(this) }
    internal val logicModifier
            by lazy { LogicModifier(this) }
    internal val logicFunction
            by lazy { LogicFunction(this) }

    // PARSER CONTEXT
    // ==============

    internal lateinit var lexer: Lexer
    internal lateinit var lexerStream: Iterator<TokenInfo>
    internal lateinit var source: String
    internal lateinit var filePath: String
    internal var memories =
        mutableMapOf<Int, MutableMap<() -> TokenAST?, Pair<TokenAST?, Int>>>()
    internal val lexemes = arrayListOf<TokenInfo>()
    internal var index = 0

    init {
        lexer?.let { link(it) }
    }

    // CONTEXT METHODS
    // ===============

    /**
     * Sets the source code to be parsed.
     * @param source The source code to be parsed.
     * @param filePath The path of the file to be parsed.
     */
    fun load(
        source: String,
        filePath: String = "terminal"
    ) {
        this.memories.clear()
        this.lexemes.clear()
        this.filePath = filePath
        this.source = source
        lexerStream = lexer.tokens()
        index = 0
    }

    /**
     * Links the parser to the lexer.
     * @param lexer The lexer to be linked.
     */
    fun link(lexer: Lexer) {
        this.lexer = lexer
    }

    /**
     * Parses the source code.
     * @return The AST of the source code.
     * @throws SyntaxError If the source code is invalid.
     */
    fun parse(): TokenAST = logicPackage.kiwiFile()
}