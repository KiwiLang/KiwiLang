# String

> All strings in Kiwi are reference types, so
> they store additional data to run faster.

Strings in Kiwi are represented by the `String` type.
It can be initialized with a string literal:

```kiwi
a: auto = "Hello, world!"
```

Elements of a string can be accessed via the `[]` operator:

```kiwi
a: auto = "Hello, world!"
b: auto = a[0]  # 'H'
c: auto = a[1]  # 'e'
```

All strings are immutable, so each method that changes a string
returns a new string object:

```kiwi
a: auto = "Hello, world!"
b: auto = a.replace("world", "Kiwi")  # "Hello, Kiwi!"
print(a)  # "Hello, world!"
print(b)  # "Hello, Kiwi!"
```


