package org.kiwilang.lang.lexical

import kotlin.io.path.relativeTo
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10

/**
 * This class represents a lexical exception.
 */
sealed class LexicalException(message: String) : Exception(message) {
    /**
     * This class represents a lexical exception with a location.
     */
    abstract class ExceptionWithLocation(
        lexer: Lexer, message: String, length: Int = 1
    ) : LexicalException(
        run {
            val filePath = lexer.file?.let {
                lexer.compiler.workingDirectory.resolve(it).relativeTo(lexer.compiler.workingDirectory).toString()
            } ?: "<unknown>"
            val offset = floor(log10(lexer.startLine.toDouble())).toInt() + 1
"""
error: $message
--> $filePath:${lexer.startLine}L:${lexer.startColumn}C
|
| ${
    arrayOf(3, 2, 1, 0).mapNotNull {
        if (lexer.startLine - it >= 1) "${
            (lexer.startLine - it).toString().padStart(
                ceil(log10(lexer.startLine.toDouble())).toInt()
            )
        }. ${
            lexer.source.lines()[lexer.startLine - it - 1]
        }" else null
    }.joinToString("\n| ")
}
| ${" ".repeat(lexer.startColumn + offset)} ${"^".repeat(length)} $message
|
| ------- lexical error
error: could not compile `$filePath` due to previous error
""".trimIndent()
        }
    ) {
        constructor(
            lexer: Lexer,
            message: String,
            match: MatchResult
        ) : this(
            lexer,
            message,
            match.value.length
        )
    }

    /**
     * This class represents an unexpected character exception.
     */
    class UnexpectedCharacterException(
        lexer: Lexer, character: Char,
    ) : ExceptionWithLocation(
        lexer,
        "Unexpected character: '$character'"
    )

    /**
     * This class represents an unexpected end of file exception.
     */
    class EndOfFileException(
        lexer: Lexer,
    ) : ExceptionWithLocation(
        lexer,
        "Unexpected end of file"
    )
}