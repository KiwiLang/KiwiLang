# Numbers

## Integer types

Minecraft has only one full integer type - `int`. It's a 32-bit signed integer type,
which can store values from `-2,147,483,648` to `2,147,483,647`.
So keep in mind that all another integer types are just aliases for `int`.

Kiwi provides a set of built-in types that represent numbers.
For integers, there are three types with different sizes and ranges:

| Type    | Size (bits) | Min value      | Max value     |
|---------|-------------|----------------|---------------|
| `byte`  | 8           | -128           | 127           |
| `short` | 16          | -32,768        | 32,767        |
| `int`   | 32          | -2,147,483,648 | 2,147,483,647 |

When you initialize a variable with no explicit type specification,
the compiler automatically infers the type from the value:

```kiwi
a = 1  # type is int
b = 2b  # type is byte
c = 3s  # type is short
```

> Note that there is no `long` type in Kiwi. It's because Minecraft doesn't support 64-bit integers.


