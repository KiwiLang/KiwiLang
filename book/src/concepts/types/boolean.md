# Boolean

The `Bool` type represents boolean values. There are only two possible values:
`true` and `false`.

```kiwi
a: auto = true
b: Bool = false
```

Booleans are primitive types, so they are stored by value without any reference.

Built-in operators for booleans:

| Precedence | Operator | Description |
|:-----------|----------|-------------|
| Highest    | `!`      | Logical NOT |
|            | `&&`     | Logical AND |
| Lowest     | `\|\|`   | Logical OR  |

```kiwi
a: auto = !true  # false
b: auto = true && false  # false
c: auto = true || false  # true
```

`||` and `&&` operators work lazily. It means that the right operand is evaluated
only if the left operand is not enough to determine the result of the expression.

```kiwi
a: auto = amIAlive() || amIDead()  
# amIDead() is not called if amIAlive() returns true
```
