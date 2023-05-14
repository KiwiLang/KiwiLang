# Conditions

## If expression

In Kiwi, `if` is an expression: it returns a value.
But there is ternary operator in Kiwi if you prefer to use it instead.

```kiwi
a: Int = 1
b: Int = 2
lateinit max: Int

// With else
if a > b {
    max = a
} else {
    max = b
}

// As expression (brackets in condition are required)
max = if (a > b) a else b

// As expression with blocks
max = if a > b {
    a
} else {
    b
}

// With ternary operator
max = a > b ? a : b
```

In case of `if` used as expression with blocks, 
the last expression in the block is returned.

```kiwi
max: Int = if a > b {
    println("a is max")
    a
} else {
    println("b is max")
    b
}
```

## When expression

`when` defines a conditional expression with multiple branches.
It is similar to `switch` in other languages.

```kiwi
when x {
    1 -> println("x == 1")
    2 -> println("x == 2")
    else -> println("x is neither 1 nor 2")
}
```

### When expression with no argument

`when` can be used as an alternative to `if-else if` chain.
Note the fact, that it can be used as an expression.

```kiwi
fun maxOfTwo(x: Int, y: Int) -> Int = when {
    x > y -> x
    else -> y
}
```

Or as a statement.

```kiwi
when {
    x.isOdd() -> println("x is odd")
    x.isEven() -> println("x is even")
    else -> println("x is funny")
}
```

### When expression with argument

It is also possible to use `when` with an argument.
In this case, the argument is compared with given keys.

```kiwi
fun describe(obj: Int) -> Int {
    return when obj {
        1 -> "One"
        2 -> "Two"
        else -> "Unknown"
    }
}
```

To define several keys for one branch, you can use comma-separated list of values.

```kiwi
fun describe(obj: Int) -> Int {
    return when obj {
        1, 2 -> "One or Two"
        else -> "Unknown"
    }
}
```

You can also use arbitrary expressions (not only constants) as branch keys.

```kiwi
fun describe(obj: Int) -> Int {
    return when obj {
        parseInt(s) -> "s encodes obj"
        else -> "s does not encode obj"
    }
}
```

Keep in mind, in case of arbitrary expressions, the order of branches is important.

```kiwi
a: Int = 1

when x {
    1 -> println("x == 1")
    a -> println("x == a")
    else -> println("otherwise")
}
# prints "x == 1" - the first branch is matched
```

It's possible to define variable using following syntax.

```kiwi
fun describeHealth(player: Selector) =
    when health: Int = player.health {
        20 -> "is healthy"
        in 10..19 -> f"needs to restore {20 - health} health points"
        else -> "is having troubles"
    }
```

