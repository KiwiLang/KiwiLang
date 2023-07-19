package org.kiwilang

import org.jline.utils.*
import org.kiwilang.lang.Compiler
import org.kiwilang.util.*
import kotlin.io.path.Path

fun main() {
    val sourceFile = Path("src/main/resources/debug.kiwi")
    try {
        val compiler = Compiler()
        val tokens = Iterable { compiler.lexer.load(sourceFile).tokenize() }.toList()
        tokens.print()
    } catch (e: Exception) {
        val msg = AttributedStringBuilder()
            .append(e.message, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
            .toAnsi()
        println(msg)
    }
}