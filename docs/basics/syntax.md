# Basic syntax

This is a collection of basic syntax examples. For more advanced examples, see the [examples](../examples) directory.

Documentation is not yet complete. For more information, see the [README](../../README.md).

## Package definition and imports

Package specification is required to be at the top of the file.
Imports should be placed after the package specification.

```kiwi
package my_package  # package is optional feature

import my_package.object  # you can specify the path to the file

import my_package.*  # in order to import all objects from the package

import my_package.my as my_alias  # rename object

// ...
```

## Program entry point

There is no `main` function in Kiwi. Instead, you can create any function
and link it with the `load` event.

```kiwi
fun main() <- load(always) {
    print("Hello, World!")  # outputs "Hello, World!" to the chat
}
```

It's recommended to use the `always` modifier to ensure that the function is called every time
server is reloaded.

## Print to the chat or console

`print` prints the specified text to the chat.

```kiwi
print("Hello, World!")  # outputs "Hello, World!" to the chat
print("Hello, \n", "World!")  # outputs "Hello, " and "World!" to the chat
```

`log` prints the specified text to the console. It's required to make a few steps to enable
this feature in your server. See [this page](../get-started/setting-up.md#enabling-logging) for more information.

```kiwi
log("Debug message")  # outputs "Debug message" to the console
```

## Variables

Variables are declared by unique name and type.

```kiwi
a: int = 1  # variable declaration
```

You can also declare a variable specifying `auto` type. In this case, the type will be inferred from the value.

```kiwi
a: auto = 1  # type is int
b: int  # declaring variable without value
b = 4  # assigning value to the variable
```

Important feature of Kiwi is compile-time logic. You can create
compile-time variables using '$' sign in type.

```kiwi
a: $int = 1  # type is compile-time int
b: $auto = 2  # type is compile-time int
```

Of course, you can use compile-time variables in runtime.

```kiwi
a: $int = 1
b: int = a  # type is int
```

But you can't use runtime variables in compile-time.

```kiwi
a: int = 1
b: $int = a  # error: can't use runtime variable in compile-time
```

## Functions

A function with two parameters and return type `int`.

```kiwi
fun sum(x: int, y: int) -> int {
    return x + y
}
```

A function can be declared as expression

```kiwi
fun sum(x: int, y: int) -> int = x + y
```

A function can be declared as compile-time

```kiwi
compiletime fun sum(x: $int, y: $int) -> $int = x + y
```

Or even inline runtime function

```kiwi
inline fun sum(x: int, y: int) -> int = x + y
```

## Creating classes and instances

To define a class, use the `class` keyword.

```kiwi
class MyClass
```

Properties of a class can be listed in its declaration or body.

```kiwi
class Rectangle(
    store width: int,
    store height: int
) {
    store area: int = width * height
}
```

There are a lot of ways to make constructors due to data packs limitations.

```kiwi
predeclared class Rectangle(
    # First priority
    width: int as component0,
    height: int as component1
) {
    # Second priority
    store width: int = 0
    store height: int = 0
    
    # Third priority
    inline init {
        width = width
        height = height
    }
     
    # Zero priority
    setup {
        width = -5
        height = -5
    }
}
```

To create an instance of a class, use constructor invocation

```kiwi
a: Rectangle = Rectangle(5, 10)
a: auto = Rectangle(5, 10)
```

Or there are shortcuts for this

```kiwi
a: Rectangle(5, 10)
a: Rectangle = 5, 10
```

Sometimes initialization value syntax sugar can be helpful to organize your code

```kiwi
a: Array = [1, 2, 3]
a: Set = {1, 2, 3}
a: Dict = {1: 2, 3: 4}
a: Criteria = dummy
```

## Comments

Just like in Python, comments start with a `#` and go until the end of the line.

```kiwi
# This is a comment
``` 

Or you can use multi-line docstrings

```kiwi
a: int = 0
"""
It's a docstring
"""
```

## String formatting

You can insert values into strings.

```kiwi
a: int = 5
b: auto = f"Hello, {a}!"  # "Hello, 5!"
```

It's particularly useful when it comes about custom commands,
you can insert compile-time variables into the command.

```kiwi
a: $int = 5
$execute("say Hello, $a!")  # first way
$execute("say Hello, ${a}!")  # second way
```

## Conditionals

```kiwi
fun maxOfTwo(x: int, y: int) -> int {
    if x > y {
        return x
    } else {
        return y
    }
}
```

In Kiwi, you can also use ternary operator.

```kiwi
fun maxOfTwo(x: int, y: int) -> int = x > y ? x : y
```

## for loops

```kiwi
fun sumOfArray(array: Array) -> int {
    var sum: int = 0
    for index in array.indices {
        sum += array[index]
    }
    return sum
}
```

Or you can iterate over the array directly.

```kiwi
fun sumOfArray(array: Array) -> int {
    var sum: int = 0
    for element in array {
        sum += element
    }
    return sum
}
```

## while loops

```kiwi
fun sumOfArray(array: Array) -> int {
    var sum: int = 0
    var index: int = 0
    while index < array.size {
        sum += array[index]
        index += 1
    }
    return sum
}
```

## when expression

```kiwi
fun describe(obj: int) -> int {
    return when obj {
        1 -> "One"
        2 -> "Two"
        3 -> "Three"
        else -> "Unknown"
    }
}
```

## Ranges

Iterate over a range.

```kiwi
for i in 1..10 {  # includes 10
    print(i)  # 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
}
```

Using a step in a range.

```kiwi
for i in 1..10 step 2 {
    print(i)  # 1, 3, 5, 7, 9
}
```

Check if a number is within a range using `in` operator.

```kiwi
fun isInRange(x: int) -> bool = x in 1..10
```

Check if a number is out of range.

```kiwi
fun square(x: int) -> int {
    if x in short.MAX... {
       log("Number is too big")
    }
    return x * x
}
```

## Exceptions

There are no classic exceptions in Kiwi, but you can use `unreliable` modifier.

```kiwi
unreliable fun foo() {
    a: int = 5
    b: int = 0
    a / b  # error: division by zero
}
```

It will check all unreliable calls inside the function and if any of them fails,
it will fail the whole function.

```kiwi
foo() ?: log("foo failed")  # foo failed
```

Function without `unreliable` modifier can't throw an exception, but you can use '?:' operator
to handle exception.

## Events

You can create an event using function with `promise` return type.

```kiwi
fun onPlayerJoin(
    player: selector = @a
) -> promise<selector> <- tick(1) {
    static event: scoreboard = minecraft.custom:minecraft.leave_game
    if (event[player] > 0) {
        event[player] = 0
        return $promise(player)
    }
}
```
