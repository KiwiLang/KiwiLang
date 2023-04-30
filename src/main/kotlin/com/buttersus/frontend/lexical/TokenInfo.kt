package com.buttersus.frontend.lexical

data class TokenInfo (
    val type: TokenType,
    val lexeme: String,
    val line: Int = 0,
    val endLine: Int = 0,
    val column: Int = 0,
    val endColumn: Int = 0,
    val index: Int = 0,
    val endIndex: Int = 0
) {
    constructor(type: TokenType, lexeme: String, lexer: Lexer, started: Triple<Int, Int, Int>)
            : this(type, lexeme,
        started.first, lexer.line,
        started.second, lexer.column,
        started.third, lexer.index)
    constructor(type: TokenType, lexer: Lexer)
            : this(type, "",
        lexer.line, lexer.line,
        lexer.column, lexer.column,
        lexer.index, lexer.index)

    override fun toString(): String {
        var type = this.type.toString()
        type = if (type.length > 10) {
            type.take(8) + ".."
        } else type.padEnd(10)
        var lexeme = this.lexeme.replace("\n", "\\n")
        lexeme = if (lexeme.length > 10) {
            lexeme.take(8) + ".."
        } else lexeme.padEnd(10)
        return "TokenInfo(type=$type, lexeme='$lexeme', line=$line, endLine=" +
                "$endLine, column=$column, endColumn=$endColumn, index=$index, endIndex=$endIndex)"
    }
}
