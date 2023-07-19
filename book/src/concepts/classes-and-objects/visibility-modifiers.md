# Visibility modifiers

All top-level declarations, classes, functions, properties, etc.
can have visibility modifiers:

- `public` - visible everywhere
- `private` - visible only inside the file or class
- `protected` - visible only inside the subclasses
- `internal` - visible only inside the package

On this page, you'll learn how to use them.

## Packages

Functions, properties, classes, objects, and interfaces can be declared
at the "top level", i.e. directly inside a package:

```kiwi
package org.example

fun foo() { ... }
class Bar { ... }
```

Such declarations are visible everywhere inside the package, because
`public` modifier is applied by default.

There are t three top-level visibility modifiers: `private` and `internal`:

- `public` means visible everywhere
- `private` means visible inside this file only (including all the declarations inside it)
- `internal` means visible inside the same module only

## Classes members

For members declared inside a class:

- `public` means visible to everyone who can see the class
- `private` means visible inside this class only
- `protected` means visible inside this class and its subclasses
- `internal` means visible to everyone in the same module who can see the class

If you override a `protected` or `internal` member and do not specify the visibility explicitly,
the overriding member will have the same visibility as the original declaration.

Examples:

```kiwi
open class Outer {
    private val a = 1
    protected open val b = 2
    internal val c = 3
    val d = 4  // public by default

    protected class Nested {
        public val e: Int = 5
    }
}

class Subclass : Outer() {
    // a is not visible
    // b, c and d are visible
    // Nested and e are visible

    override val b = 5   // 'b' is protected
    override val c = 6   // 'c' is internal
}
```

## Local declarations

Local variables, functions and classes 
can not have visibility modifiers.
