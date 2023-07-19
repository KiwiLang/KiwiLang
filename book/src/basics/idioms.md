# Idioms

A collection of random idioms that are used in Kiwi.

## Program entry point

There is no `main()` function in Kiwi. It's caused by the fact that Minecraft is a game, so
usually we use events to run our code.

So, there are a few ways to run your code:

- `onLoad(always)` event. It's called when the game is loaded, it will be
called more than once if you reload the game. It's useful for debugging.
_see [onLoad event](../standard-library/events.md#onload)._
- `onLoad(once)` event. It's called when the game is loaded for the first time.
It's useful to set up your libraries, objects, etc.
_see [onLoad event](../standard-library/events.md#onload)._
- `onTick(x: $Int)` event. It's called every x ticks. It's useful for game logic, creating
custom entities, etc.
_see [onTick event](../standard-library/events.md#ontick)._
- Calling function by yourself. To do this, you need to _"fix"_ the name of function,
that you want to call. It's required because Kiwi usually renames functions in order to
avoid name collisions. To fix the name, you need to add `@FixName("name")` annotation
like this:

```kiwi
@FixName("my_function")
fun myFunction() {
    print("Hello, World!")
}
```

Then you can call this function in minecraft chat using next syntax:

```mcfunction
function <datapack-name>:fixed/<fixed-name>
```

For example, if your datapack name is `my_datapack` and you fixed the name of function
to `my_function`, then you can call it using this command:

```mcfunction
function my_datapack:fixed/my_function
```

_see [Fix Name annotation](../standard-library/annotations.md#fix-name)._

## Comments and Docstrings
