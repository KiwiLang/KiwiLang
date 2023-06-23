package com.kiwilang.frontend

private const val TAB_SIZE = 4

fun String.tab(size: Int = 1): String {
    return this.lines().joinToString("\n") { " ".repeat(size * TAB_SIZE) + it }
}
