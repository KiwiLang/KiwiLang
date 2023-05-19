# Classes

Classes in Kiwi are declared using the keyword `class`:

```kiwi
class Person { ... }
```

The class declaration consists of the class name,
the class header (specifying its primary constructor and some other things),
and the class body, which contains the class members. Both
the header and the body are optional.

```kiwi
class Empty

class Person (
    name: String
    age: Int
)
```

## Constructors

Classes in Kiwi can have a **primary constructor**
and one or more **secondary constructors** using following syntax.

```kiwi
class Person(firstName: String) { ... }
```

The primary constructor cannot contain any code.
Initialization code can be placed in initializer blocks,
which are prefixed with the `init` keyword.

During the initialization of an instance,
the initializer blocks are executed in the 
same order as they appear in the class body, 
interleaved with the property initializers:

```kiwi
class InitOrderDemo(name: String) {
    firstProperty: auto = "First property: $name".also(::print)

    init {
        println("First initializer block that prints ${name}")
    }

    secondProperty: auto = "Second property: ${name.length}".also(::print)

    init {
        println("Second initializer block that prints ${name.length}")
    }
}
```

Primary constructor parameters can be used in the initializer blocks.
They can also be used in property initializers declared in the class body:

```kiwi
class Customer(name: String) {
    customerKey: auto = name.toUpperCase()
}
```

Primary constructors also can store parameters as properties
using any visibility modifier (by default, they are `public`):

```kiwi
class Person(
    public firstName: String,
    public lastName: String,
    public age: Int,  # trailing comma is allowed
) { ... }
```

You can add syntactic sugar to constructor parameters.

```kiwi
class Person(
    public (firstName, lastName): String by components,
    public age: Int by components = -1,
) { ... }
```

Learn more about [parameter delegation](parameter-delegation.md).

As with functions, you can add a default
parameter values.

```kiwi
class Person(
    public firstName: String = "John",
    public lastName: String = "Doe",
    public age: Int = 0,
) { ... }
```

Learn more about [visibility modifiers](visibility-modifiers.md).

## Secondary constructors

The class can also declare secondary constructors,
which are prefixed with the `constructor` keyword:

```kiwi
class Person {
    constructor(parent: Person) {
        parent.children.add(this)
    }
}
```

If the class has a primary constructor, each 
secondary constructor needs to delegate to the primary constructor,
either directly or indirectly through another secondary constructor(s).
Delegation to another constructor of the same class is done using the `this` keyword:

```kiwi
class Person(val name: String) {
    constructor(name: String, parent: Person)
        : this(name)  # delegate to primary constructor
    {
        parent.children.add(this)
    }
}
```

Or you can call primary constructor directly:

```kiwi
class Person(val name: String) {
    constructor(name: String, parent: Person)
    {
        parent.children.add(this)
        this(name)  # call primary constructor
    }
}
```

Note that code in initializer blocks will be executed
after the primary constructor and before secondary constructors:

```kiwi
class Constructors {
    init {
        println("Init block")
    }

    constructor(i: Int) {
        println("Constructor")
    }
}
```

If a non-abstract class does not declare any constructors,
it will have default constructor with no arguments.

If you don't want your class to have a public constructor,
declare an empty primary constructor with private modifier:

```kiwi
class DontCreateMe private { ... }
```

## Transfer constructor

By default, Kiwi have no references/pointers.
So, Kiwi implements copy-by-value concept.

That means that every time you declare a variable,
you create a new object in memory with the same value.

```kiwi
class Person(
    public (name): String by components
) { ... }

fun main() <- onLoad(always) {
    person1: auto = "John"
    person2: auto = person1  # create a copy of person1
}
```

```admonish note
All properties will be copied from `person1` to `person2`.
```

If you want to override this behavior,
you can use the `transfer` keyword:

```kiwi
class Person(
    public (name): String by components
) {
    transfer(other: Person) {
        name = other.name
    }
}
```

## Creating instances of classes



