package org.kiwilang.lang.lexical

import org.jline.utils.*
import org.kiwilang.preprocessor.ClassVersion
import org.kiwilang.util.*

/**
 * This class represents a token.
 */
@ClassVersion("1.0.0")
@Suppress("MemberVisibilityCanBePrivate")
sealed class TokenInfo {
    abstract val name: String

    // PROPERTIES
    // ==========

    var string: String
    var index: Int = -1
    var line: Int = -1
    var column: Int = -1
    var endIndex: Int = -1
    var endLine: Int = -1
    var endColumn: Int = -1

    // Getters
    val length: Int
        get() = endIndex - index + 1

    // CONSTRUCTORS
    // ============

    constructor(lexer: Lexer) {
        this.string = ""
        this.index = lexer.startIndex
        this.endIndex = lexer.endIndex
        this.line = lexer.startLine
        this.endLine = lexer.endLine
        this.column = lexer.startColumn
        this.endColumn = lexer.endColumn
    }

    constructor(lexer: Lexer, match: MatchResult) {
        lexer.consume(match)
        this.string = match.value
        this.index = lexer.startIndex
        this.endIndex = lexer.endIndex
        this.line = lexer.startLine
        this.endLine = lexer.endLine
        this.column = lexer.startColumn
        this.endColumn = lexer.endColumn
    }

    // METHODS
    // =======

//     companion object {
//        abstract val regex: Regex
//        internal inline fun <reified T : TokenInfo> consume(lexer: Lexer): Iterator<T> = iterator {
//            val constructor = T::class.constructors.firstOrNull {
//                it.parameters.size == 2
//                        && it.parameters[0].type == Lexer::class
//                        && it.parameters[1].type == MatchResult::class
//            } ?: throw IllegalArgumentException("TokenInfo must have a constructor with 2 parameters")
//            yield(constructor.call(lexer, regex.matchAt(lexer.source, lexer.endIndex)))
//        }
//    }

    override fun toString(): String =
        AttributedStringBuilder()
            .append(
                name.magicPadStart(10), AttributedStyle.DEFAULT
                    .foreground(AttributedStyle.MAGENTA).background(AttributedStyle.BLACK)
            )
            .append('|')
            .append(
                string.magicPadCenter(20), AttributedStyle.DEFAULT
                    .foreground(AttributedStyle.GREEN)
            )
            .append('|')
            .append(
                "L$line".magicPadEnd(4), AttributedStyle.DEFAULT
                    .foreground(AttributedStyle.BLUE)
            )
            .append(
                ":$endLine".magicPadEnd(4), AttributedStyle.DEFAULT
                    .foreground(AttributedStyle.BLUE)
            )
            .append('|')
            .append(
                "C$column".magicPadEnd(4), AttributedStyle.DEFAULT
                    .foreground(AttributedStyle.BLUE)
            )
            .append(
                ":$endColumn".magicPadEnd(4), AttributedStyle.DEFAULT
                    .foreground(AttributedStyle.BLUE)
            )
            .append('|')
            .append(
                "I$index".magicPadEnd(4), AttributedStyle.DEFAULT
                    .foreground(AttributedStyle.BLUE)
            )
            .append(
                ":$endIndex".magicPadEnd(4), AttributedStyle.DEFAULT
                    .foreground(AttributedStyle.BLUE)
            )
            .append('|')
            .toAnsi()

    class TokenNumber(
        lexer: Lexer,
        match: MatchResult
    ) : TokenInfo(lexer, match) {
        val integer = match.groups[1]?.value
        val fraction = match.groups[2]?.value
        val exponent = match.groups[3]?.value
        val postfix = match.groups[4]?.value

        override val name = "NUMBER"
//        override val regex = Regex("(\\d*)(\\.\\d*)?(?<=\\d)(e[+-]?\\d+)?(\\w*)\\b")
    }

    class TokenString(
        lexer: Lexer,
        match: MatchResult
    ) : TokenInfo(lexer, match) {

        override val name = "STRING"
//        override val regex = Regex("([\"'])(?:(?!\\1)[^\\\\]|\\\\.)*\\1")
    }

    class TokenDocString(
        lexer: Lexer,
        match: MatchResult
    ) : TokenInfo(lexer, match) {

        override val name = "DOCSTRING"
//        override val regex = Regex("([\"']{3})(?:(?!\\1)[^\\\\]|\\\\.)*\\1")
    }

    class TokenIdentifier(
        lexer: Lexer,
        match: MatchResult
    ) : TokenInfo(lexer, match) {

        override val name = "IDENTIFIER"
//        override val regex = Regex("[\\p{L}_][\\p{L}0-9_]*\\b")
    }

    class TokenEndOfFile(lexer: Lexer) : TokenInfo(lexer) {
        override val name = "EOF"
//        override lateinit var regex: Regex
    }
}
