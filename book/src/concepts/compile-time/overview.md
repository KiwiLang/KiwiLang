# Main idea of compile-time concept

It's a very important and unique concept in Kiwi. Instead of
having _boilerplate code for primitive logic_ (_control flow, data types, e.t.c_)
_inside of compiler_.

Kiwi implements it as a _library_ without any
special compiler support.

## How it works

Usually, you write code in other languages, and then compiler compiles it
to runnable code. Let's take a look at default language behavior:

```mermaid
graph TD;
    A-->B;
    A-->C;
    B-->D;
    C-->D;
```