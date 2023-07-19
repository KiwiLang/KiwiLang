# Functions

In Kiwi, you can declare function using `fun` keyword.

```kiwi
fun main() {
    println("Hello, World!")
}
```

## Function usage

Functions can be called using `()` operator.

```kiwi
fun main() {
    println("Hello, World!")
    a: Int = 4.toConstant()
}
```

### Function arguments

Functions can have arguments.

```kiwi
foo(5, 6)
```

They can be put in order or named.

```kiwi
foo(5, b = 6)
```

But named arguments must be put after ordered ones.

```kiwi
foo(a = 5, 6)  # Error
```

### Function parameters

Function parameters are defined using Pascal notation - name: type. Parameters are separated using commas,
and each parameter must be explicitly typed:

```kiwi
fun foo(a: Int, b: Int) {
    print(a + b)
}
```

You can leave comma after last parameter optionally.

```kiwi
fun foo(
    a: Int, 
    b: Int,  # trailing comma
) {
    print(a + b)
}
```

### Default arguments

Function parameters can have default values.

```kiwi
fun foo(a: Int = 5, b: Int = 6) {
    print(a + b)
}
```

And you can call function without arguments, which will use default values.

```kiwi
foo() # prints 11
```

### Variable number of arguments (varargs)

Function parameters can be defined as varargs.

```kiwi
fun foo(vararg array: Int) {
    for (item in a) {
        print(i)
    }
}
```

And you can pass any number of arguments to it.

```kiwi
foo(1, 2, 3, 4, 5)  # prints 1 2 3 4 5 separated by newlines
```

Varargs must be the last parameter.

```kiwi
fun foo(vararg array: Int, a: Int) {  # Error
    for (item in a) {
        print(i)
    }
}
```

Also, you can pass array to varargs by using `*` operator.

```kiwi
a: Array<Int> = [1, 2, 3, 4, 5]
foo(*a)  # prints 1 2 3 4 5 separated by newlines
```
