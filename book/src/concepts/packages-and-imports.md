# Packages and imports

A source file may start with package declaration:

```kiwi
package org.example

class Foo {
    # ...
}
```

All the contents, such as classes and functions, 
of the source file are included in this package.
So, in the example above, you can refer to the 
class `Foo` as `org.example.Foo`.

If you don't specify a package, the source file
is included in the default package.

## Default imports

The following packages are imported by default:

- `kiwi.*`

## Imports

Apart from the default imports, each file may contain its own import directives.

You can import either a single name:

```kiwi
import org.example.Foo  # Foo is now available in the current scope
```

or all the names from a package:

```kiwi
import org.example.*  # All the names from org.example are now available in the current scope
```

You can also import a name under a different name:

```kiwi
import org.example.Foo as Bar  # Foo is now available as Bar in the current scope
```

## Visibility of top-level declarations

Top-level declarations are visible in the whole package by default.
You can make them visible only inside the package with the `private` modifier:

```kiwi
private class Foo {
    # ...
}
```

_see [Visibility modifiers](classes-and-objects/visibility-modifiers.md)_
