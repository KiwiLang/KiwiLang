# Number

> All numbers in Kiwi are primitive types. Generally, it means that
> they are stored without any additional data to save memory.

## Integer types

Minecraft has only one full integer type - `Int`. It's a 32-bit signed integer type,
which can store values from `-2,147,483,648` to `2,147,483,647`.
So keep in mind that all another integer types are just aliases for `Int`.

Kiwi provides a set of built-in types that represent numbers.
For integers, there are three types with different sizes and ranges:

| Type    | Size (bits) | Min value      | Max value     |
|---------|-------------|----------------|---------------|
| `Byte`  | 8           | -128           | 127           |
| `Short` | 16          | -32,768        | 32,767        |
| `Int`   | 32          | -2,147,483,648 | 2,147,483,647 |

When you initialize a variable with no explicit type specification,
the compiler automatically infers the type from the value:

```kiwi
a: auto = 1  # type is int
b: auto = 2b  # type is byte
c: auto = 3s  # type is short
```

> Note that there is no `long` type in Kiwi. It's because Minecraft
> doesn't support arithmetic for 64-bit integers.

## Floating-point types

Kiwi can simulate floating-point types using fixed-point arithmetic.
So there are two floating-point types in Kiwi: `float` and `double`.

| Type     | Size (bits) | Integer bits | Fractional bits | Min value | Max value |
|----------|-------------|--------------|-----------------|-----------|-----------|
| `Float`  | 32          | 24           | 8               | -8388608  | 8388607   |
| `Double` | 32          | 16           | 16              | -32768    | 32767     |

You can initialize floating-point variables with numbers having
a fractional part:

```kiwi
a: auto = 1.0f  # type is float
b: auto = 2.0d  # type is double
c: auto = 3.0   # type is double
```

Or you can use scientific notation:

```kiwi
a: auto = 1.0e-3f
```

Note the fact that Kiwi does not automatically to convert floating-point 
numbers to integers and back. Therefore, you will have to do this manually:

```kiwi
fun printDouble(d: Double) { print(d) }

printDouble(1)  # error: can't pass double to printDouble
printDouble(1d)  # ok
```

## Literal constants for numbers

There are several ways to write literal constants for numbers:

- Decimal: `123`
- Hexadecimal: `0x123`
- Binary: `0b101`
- Octal: `0o123`
- Scientific notation: `1.0e-3`
- Underscores: `1_000_000`
- Suffixes: `1b`, `1s`, `1`, `1.0f`, `1.0d`
- Negative numbers: `-1`, `-1.0f`, `-1.0d`
- Positive numbers: `+1`, `+1.0f`, `+1.0d`
- Infinity: `inf`, `+inf`, `-inf` (only for compile-time constants)
