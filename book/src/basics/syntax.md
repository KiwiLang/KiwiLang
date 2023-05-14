# Basic syntax

This is a collection of basic syntax concepts of Kiwi.
If you are similar with Kotlin, it will be easy to understand.

_see [Examples here](../examples/overview.md)._

## Package definition and imports

Package specification is required to be at the top of the file.
Imports should be placed after the package specification.

```kiwi
package my_package  # package specification

import my_package.object  # import object from another package

import my_package.*  # in order to import all objects from the package

import my_package.my as my_alias  # import object with alias

...  # your code
```

_see [Packages and Imports](../concepts/packages-and-imports.md)._

## Program entry point

There is no `main` function in Kiwi. Instead, you can create any function
and link it with the `onLoad` event.

```kiwi
fun main() <- onLoad(always) {
    print("Hello, World!")  # outputs "Hello, World!" to the chat
}
```

_see [Explanation here](idioms.md#program-entry-point)._

## Print to the chat or console

`print` prints the specified text or value to the chat.

```kiwi
print("Hello, World!")  # outputs "Hello, World!" to the chat
print("Hello, \n", "World!")  # outputs "Hello, " and "World!" to the chat
print(3)  # outputs "3" to the chat
```

`log` prints the same way, but to the debugger console.
It's required to make a few steps to enable
this feature in your server.

```kiwi
log("Debug message")  # outputs "Debug message" to the console
```

_see [Debugging](../tools/kiwi-debugger.md)._

## Variables

Variables are declared by unique name and type.

```kiwi
a: Int = 1  # variable declaration
```

You can also declare a variable specifying `auto` type. In this case, the type will be inferred from the value.

```kiwi
a: auto = 1  # type is Int
b: Int  # declaring variable without value
b = 4  # assigning value to the variable
```

Important feature of Kiwi is compile-time logic. You can create
compile-time variables using '$' sign in type.

```kiwi
a: $Int = 1  # type is compile-time Int
b: $auto = 2  # type is compile-time Int
```

Of course, you can use compile-time variables in runtime.

```kiwi
a: $Int = 1
b: Int = a  # b is 1
```

But you can't use runtime variables in compile-time.

```kiwi
a: Int = 1
b: $Int = a  # error: can't use runtime variable in compile-time
```

_see [Variables](../concepts/variables.md)._

## Functions

A function with two parameters and return type `Int`.

```kiwi
fun sum(x: Int, y: Int) -> Int {
    return x + y
}
```

A function can be declared as expression

```kiwi
fun sum(x: Int, y: Int) -> Int = x + y
```

Also, it can be declared as compile-time

```kiwi
compiletime fun sum(x: $Int, y: $Int) -> $Int = x + y
```

Or even inline runtime function

```kiwi
inline fun sum(x: Int, y: Int) -> Int {
    return x + y
}
```

_see [Functions](../concepts/functions/functions.md), 
[Compile-time Functions](../concepts/functions/compile-time-functions.md) and
[Inline Functions](../concepts/functions/inline-functions.md)._

## Creating classes and instances

To define a class, use the `class` keyword.

```kiwi
class MyClass
```

Properties of a class can be listed in its declaration or body.

```kiwi
class Rectangle(
    public width: Int,
    public height: Int
) {
    area: Int = width * height
}
```

By default, all parameters in the constructor are not stored in the class.
To store them, just add any visibility modifier (such as `public`).

To create an instance of a class, use constructor invocation

```kiwi
a: Rectangle = Rectangle(5, 10)
a: auto = Rectangle(5, 10)
a: Rectangle(5, 10)
```

Or you can add parameter delegation using `by` keyword

```kiwi
class Rectangle(
    public (width, height): Int by components  # parameter delegation
) {
    area: Int = width * height
}

a: Rectangle = 5, 10
```

Parameter delegation is a powerful feature that allows you to organize your code

```kiwi
a: Array = [1, 2, 3]
a: Set = {1, 2, 3}
a: Dict = {1: 2, 3: 4}
a: Criteria = dummy
```

_see [Classes](../concepts/classes-and-objects/classes.md) 
and [Parameter Delegation](../concepts/classes-and-objects/parameter-delegation.md)._

## Comments

Just like in Python, comments start with a `#` and go until the end of the line.

```kiwi
# This is a comment
``` 

Or you can use multi-line docstrings

```kiwi
a: Int = 0
"""
It's a docstring
"""
```

_see [Comments and Docstrings](idioms.md#comments-and-docstrings)._

## String formatting

You can insert values into strings, using f-strings with `{}`.

```kiwi
a: Int = 5
b: auto = f"Hello, {a}!"  # "Hello, 5!"
```

Or use another syntax for default strings when it comes to compile-time.

```kiwi
a: $Int = 5
$execute("say Hello, $a!")  # first way
$execute("say Hello, ${a}!")  # second way
```

_see [String Formatting](../concepts/string-formatting.md)._

## Conditionals

```kiwi
fun maxOfTwo(x: Int, y: Int) -> Int {
    if x > y {
        return x
    } else {
        return y
    }
}
```

In Kiwi, you can also use ternary operator followed by `?` and `:`.

```kiwi
fun maxOfTwo(x: Int, y: Int) -> Int = x > y ? x : y
```

_see [If expression](../concepts/control-flow/conditions.md#if-expression)._

## for loops

```kiwi
fun sumOfArray(array: Array) -> Int {
    sum: Int = 0
    for index in array.indices {
        sum += array[index]
    }
    return sum
}
```

Or you can iterate over the array directly.

```kiwi
fun sumOfArray(array: Array) -> Int {
    sum: Int = 0
    for element in array {
        sum += element
    }
    return sum
}
```

_see [for loops](../concepts/control-flow/loops.md#for-loops)._

## while loops

```kiwi
fun sumOfArray(array: Array) -> Int {
    sum: Int = 0
    index: Int = 0
    while index < array.size {
        sum += array[index]
        index += 1
    }
    return sum
}
```

_see [while loops](../concepts/control-flow/loops.md#while-loops)._

## when expression

```kiwi
fun describe(obj: Int) -> Int {
    return when obj {
        1 -> "One"
        2 -> "Two"
        3 -> "Three"
        else -> "Unknown"
    }
}
```

_see [when expression](../concepts/control-flow/conditions.md#when-expression)._

## Exceptions

There are no classic exceptions in Kiwi, but you can use `unreliable` modifier.

```kiwi
unreliable fun foo() {
    a: Int = 5
    b: Int = 0
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

_see [Exceptions](../concepts/control-flow/exceptions.md)._

Or if you want to throw an exception, you can use `throw` keyword.

```kiwi
fun foo() {
    throw "foo failed"
}
```

## Events

It can be used by `<- <event>` syntax.

```kiwi
fun main() <- onTick(1) {
    log("Hello, world!")
}
```

You can also get access to return parameters of the event.
To do this, you need to specify code, that will be executed when
the specified event is triggered using `trigger` keyword.

> Order of trigger blocks does matter.

```kiwi
fun main() <- onLoad(always), (time: Int) <- onTick(1) {
    log("This will be printed firstly")
    trigger onTick {
        log("Time: $time")  # this code will be executed when onTick event is triggered
    }
    trigger onLoad {
        log("Hello, world!")  # this code will be executed when onLoad event is triggered
    }
    log("This will be executed in any case")
}
```

You can create an event using function with `Promise` return type.

```kiwi
playerLeaveScoreboard: Scoreboard = minecraft.custom:minecraft.leave_game

fun onPlayerJoin(
    player: Selector
) -> Promise<Selector> <- onTick(1) {
    if playerLeaveScoreboard[player] {
        playerLeaveScoreboard[player] = 0
        return player
    }
}

fun main() <- onPlayerJoin(@a) {
    log("Player joined")
}
```

_see [Events](../concepts/functions/events.md)._

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
fun isInRange(x: Int) -> bool = x in 1..10
```

Check if a number is out of range.

```kiwi
fun square(x: Int) -> Int {
    if x in short.MAX... {
       log("Number is too big")
    }
    return x * x
}
```

_see [Ranges](../standard-library/ranges.md)._

## Collections

Iterate over a collection.

```kiwi
for (item in items) {
    print(item)
}
```

Check if a collection contains an object using `in` and `!in` operators.

```kiwi
fun isInRange(x: Int) -> bool = x in 1..10
fun isNotInRange(x: Int) -> bool = x !in 1..10
```

_see [Collections](../standard-library/collections.md)._
