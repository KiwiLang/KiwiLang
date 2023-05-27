# Variables

## Declaring variables

Variables in Kiwi have a little bit an unusual syntax.

Instead of using keywords like `var` or `let` to declare variables, 
you just specify type of the variable and its name.

```kiwi
a: Int
b: String
c: Bool
```

You can also specify the initial value of the variable:

```kiwi
a: Int = 1
b: String = "Hello"
c: Bool = true
```

It's equivalent to:

```kiwi
a: Int = Int(1)
b: String = String("Hello")
c: Bool = Bool(true)
```

Or using shorthand syntax:

```kiwi
a: Int(1)
b: String("Hello")
c: Bool(true)
```

## Transfer constructors

It's constructor that initializes a new object with the same value as the argument.

```admonish note
Each type has a transfer constructor by default.
```

See syntax:

```kiwi
a: String = "Hello"
b: String = a  # equivalent to b: String(a)
```

So, there is no references like in C++ or Java due to the fact
that Minecraft doesn't support references.

## Type 


