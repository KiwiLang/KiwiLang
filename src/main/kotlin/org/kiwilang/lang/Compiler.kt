package org.kiwilang.lang

import org.kiwilang.lang.lexical.Lexer
import kotlin.io.path.Path

class Compiler {
    // SYSTEM PROPERTIES
    // =================

    // Version
    val version = "0.0.1"

    // PROPERTIES
    // ==========

    val workingDirectory = Path(System.getProperty("user.dir"))

    // Low-level components
    val lexer = Lexer(this)
}