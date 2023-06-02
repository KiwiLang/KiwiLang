# Interpret classes

Runtime classes in Kiwi can have compile-time properties
by using the `interpret` keyword.

```kiwi
interpret class Foo {
  bar: $Int = 0
}
```

Such classes can be used as normal classes, but they provide
ways to access their compile-time properties.

For example, you can get access to compile-time properties if
it's declared at same or higher levels.

```kiwi
a: Foo = Foo()  # a is declared at the same level as property usage
a.bar = 1
print(a.bar)  # 1
```

## Restrictions

As compile-time properties are not stored in runtime,
you can't access them if the object can't be resolved at compile-time.

```kiwi
fun printBar(foo: Foo) {
  print(foo.bar)  # Error: foo.bar is not a runtime property
}
```

Also, to get access from a runtime interpret parameter,
you need to use additional annotations, that will guarantee
that the parameter is known at compile-time.

```kiwi
interpret fun printBar(
    @compileTimeKnown  # This annotation is required
    foo: Foo
) {
    print(foo.bar)  # OK
}

printBar(Foo())  # OK
```

Note that each time you interpret a
function, a new function will be created,
so it's better to avoid using it as much as possible.

_see [Compile-time known annotations](../../standard-library/compile-time-known-annotations.md)
and [Interpret functions](../compile-time/interpret-functions.md)._

## Interpret classes in types

There are a few classes in standard library that are interpreted,
and generally they are primitives:

- `Int` - integer number
- `Float` - floating point number
- `String` - string
- `Boolean` - boolean value
- `Array` - array