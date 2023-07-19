# Introduction to compile-time

Welcome to the introduction to compile-time in Kiwi!
Before you start, make sure you have read the [basic syntax](/basics/syntax.md) section.

This section involves the following topics:
- [What is compile-time?](#what-is-compile-time)
- [Time modifiers](#time-modifiers)
- [Functions](#functions)
- [Variables](#variables)
- [Classes](#classes)
- [Practical usage](#practical-usage)

So, let's get started!

## What is compile-time?

Usually, all code you write is executed at runtime. Let's take a look at a simple example:

```kiwi
fun factorial(n: Int): Int {
    if (n == 0) {
        return 1
    }
    return n * factorial(n - 1)
}
```

This function calculates the factorial of a number. It is executed at runtime,
so it can be called from other functions:

```kiwi
fun main() {
    println(factorial(5))
}
```

But what if we want to calculate the factorial of a number at compile-time?
Like if you did it calculate it yourself and wrote the result in the code.

Well, you can do it with the by adding time modifier to the function:

```kiwi
compiletime fun factorial(n: $Int): $Int {
    if (n == 0) {
        return 1
    }
    return n * factorial(n - 1)
}
```

Note, that type of the argument and return value is `$Int` instead of `Int`.

Also, you can use more universal modifier to allow the function to be executed at both compile-time and runtime:

```kiwi
anytime fun factorial(n: $Int): $Int {
    if (n == 0) {
        return 1
    }
    return n * factorial(n - 1)
}
```
