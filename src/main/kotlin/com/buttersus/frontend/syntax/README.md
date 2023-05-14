# Kiwi Grammar

## Table of Contents

* [Introduction](#Introduction)
* [Logic Parts](#Logic-Parts)
    * [Package](#Package)
    * [Declaration](#Declaration)
    * [Class](#Class)
* [Parser](#Parser)

## Introduction

Here you can learn about the grammar of Kiwi language.
Kiwi parser uses left recursive grammar,
> which uses **LL(*)** parsing algorithm.

Its grammar is very similar to [Kotlin's one](https://kotlinlang.org/docs/reference/grammar.html),
but it has some differences. There are some of them:

- _There is no runtime type checking or nullability_
- _Kiwi uses type declaration instead of `var` and `val` keywords_
- _It's more practical sometimes to use compile-time paradigm instead of runtime one_
- _It inherited some syntax sugar from Python, especially for expressions_
    - e.g. `a: $array<int> = [1, 2, 3]`
- _It implements some minecraft specific syntax sugar_
    - e.g. `block(~, ~, ~)` instead to specify relative coordinates

All the grammar expressions are described below:

| Grammar expression         | Description          | Example                  |
|----------------------------|----------------------|--------------------------|
| `statement`                | Any statement        | `importList`             |
| `<token>`                  | Any token            | `<EOF>`                  |
| `'literal'`                | Literal              | `'package'`              |
| `expression?`              | Zero or one          | `importHeader?`          |
| `expression*`              | Zero or more         | `importHeader*`          |
| `expression+`              | One or more          | `importHeader+`          |
| `?=expression`             | Positive lookahead   | `?='}'`                  |
| `?!expression`             | Negative lookahead   | `?!'semi'`               |
| `!expression`              | Necessary expression | `!identifier`            |
| `expression \| expression` | Alternative          | `'.' '*' \| importAlias` |

## Logic Parts

The Kiwi language parser is divided into several classes,
each of which is responsible for a certain part of the grammar.

| Logic part of parser             | Description               |
|----------------------------------|---------------------------|
| [LogicPackage](#Package)         | Top-Level declarations    |
| [LogicDeclaration](#Declaration) | All types of declarations |
| [LogicClass](#Class)             | Class declarations        |

### Package

See [Package implementation](LogicPackage.kt)

* **_KiwiFile:_**
  `packageHeader importList topLevelDeclaration* !<EOF>`
* **_PackageHeader:_**
  `('package' identifier !semi)?`
* **_ImportList:_**
  `importHeader*`
* **_ImportHeader:_**
  `'import' !identifier ('.' '*' | importAlias)? !semi`
* **_ImportAlias:_**
  `'as' !simpleIdentifier`
* **_TopLevelDeclaration:_**
  `declaration !semi`

### Declaration

See [Declaration implementation](LogicDeclaration.kt)

* **_Declaration:_**
  `classDeclaration | objectDeclaration | functionDeclaration | propertyDeclaration | typeAlias`
* **_ClassDeclaration:_**
  `modifiers? 'class' !simpleIdentifier typeParameters? primaryConstructor?
  (':' !delegationSpecifiers)? classBody?`
* **_ObjectDeclaration:_**
  `modifiers? 'object' !simpleIdentifier (':' !delegationSpecifiers)? classBody?`
* **_FunctionDeclaration:_**
  `modifiers? 'fun' typeParameters? (receiverType !'.')? !simpleIdentifier functionValueParameters
  ('->' type)? !functionBody`
* **_PropertyDeclaration:_**
  `modifiers? `

### Class

### Expressions

| Precedence | Title          | Symbols                      |
|------------|----------------|------------------------------|
| Highest    | Postfix        | `++` `--`                    |
|            | Prefix         | `++` `--` `-` `+` `!`        |  
|            | Multiplicative | `*` `/` `%`                  |
|            | Additive       | `+` `-`                      |
|            | Range          | `..`                         |
|            | Comparison     | `<` `>` `<=` `>=`            |
|            | Equality       | `==` `!=`                    |
|            | Conjunction    | `&&`                         |
|            | Disjunction    | `\|\|`                       |               
| Lowest     | Assignment     | `=` `+=` `-=` `*=` `/=` `%=` |

## Parser

The parser is implemented using self-written [LL(*)](https://en.wikipedia.org/wiki/LL_parser) algorithm.
Need to say that the parser can't be implemented using [LL(1)](https://en.wikipedia.org/wiki/LL_parser) algorithm,
