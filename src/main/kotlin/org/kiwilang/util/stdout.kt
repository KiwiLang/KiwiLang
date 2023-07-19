package org.kiwilang.util

/**
 * Prints the contents of an iterator.
 */
fun <T : Any> Iterator<T>.print() = this.forEach {
    println(it)
}

/**
 * Prints the contents of an iterable.
 */
fun <T : Any> Iterable<T>.print() = this.forEach {
    println(it)
}

/**
 * Inlines a string.
 */
fun String.inline() = this.replace("\n", "\\n")
    .replace("\t", "\\t")

fun String.padCenter(length: Int) = this.padStart((length - this.length) / 2 + this.length)
    .padEnd(length)

/**
 * Returns result of block if condition is true, otherwise returns this.
 */
fun <T: Any> T.letIf(condition: Boolean, block: (T) -> T): T =
    if (condition) block(this) else this

/**
 * Executes block if condition is true, otherwise returns this.
 */
fun <T: Any> T.alsoIf(condition: Boolean, block: (T) -> Unit): T {
    if (condition) block(this)
    return this
}

/**
 * Limits the length of a string.
 */
fun String.limitStart(length: Int) = if (this.length > length) {
    "..." + this.substring(this.length - length + 3, this.length)
} else {
    this
}

/**
 * Limits the length of a string.
 */
fun String.limitEnd(length: Int) = if (this.length > length) {
    this.substring(0, length - 3) + "..."
} else {
    this
}

/**
 * Inlines a string, limits its length, and pads it.
 */
fun String.magicPadStart(length: Int) = this.inline().limitEnd(length).padStart(length)

/**
 * Inlines a string, limits its length, and pads it.
 */
fun String.magicPadEnd(length: Int) = this.inline().limitEnd(length).padEnd(length)

/**
 * Inlines a string, limits its length, and pads it.
 */
fun String.magicPadCenter(length: Int) = this.inline().limitEnd(length).padCenter(length)
