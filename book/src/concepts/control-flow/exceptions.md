# Exceptions

## Exception calsses

All exception classes in Kiwi inherit from `Exception` class.
Every exception has a message, stack trace and optional cause.

To throw an exception, you need to use `throw` keyword.

```kiwi
throw Exception("foo failed")
```

To catch an exception, you need to use `try` ... `catch` block.

```kiwi
try {
    # some code
} catch e: Exception {
    # handle exception
} finally {
    # optional finally block
}
```

There may be zero or more `catch` blocks, but only one `finally` block.
However, at least one of them must be present.

### Try is an expression

`try` is an expression, i.e. it may have a return value.

```kiwi
val a: Int = try {
    foo()
} catch e: Exception {
    0
}
```

The returned value of `try` is either the last expression in the `try` block
or the last expression in the `catch` block (or blocks).
The `finally` block does not affect the result of the expression.

## The Nothing type

`throw` is an expression in Kiwi, so you can use it on the right hand side of an assignment.

```kiwi
a: Int = if (x >= 0) {
    sqrt(x)
} else {
    throw Exception("x must be non-negative")
}
```

The `throw` expression has type `Nothing`. It has no value and is used to mark
unreachable code.

```kiwi
fun fail(message: String): Nothing {
    throw Exception(message)
}
```

