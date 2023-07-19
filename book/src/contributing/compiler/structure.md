# Structure

[/img]: /assets/kotlin-logo.svg "48px"

The compiler is written in Kotlin and is split into multiple modules:

- `kotlin-compiler` - The main compiler module.

The compiler is split into two parts: the frontend and the backend.

```mermaid
graph LR
    USER["User"] --> Frontend
    %% |Source code|
    subgraph Frontend
        direction TB
        VOID_FRONTEND[ ]:::empty -->|Source code| LEXER
        LEXER["Lexical analysis"] -->|Lexems| PARSER["Syntax analysis"]
        PARSER -->|Abstract Syntax Tree| SEMANTICS["Semantic analysis"]
        
    end
    Frontend --> Backend
    %% |Abstract Syntax Tree + Mapping|
    subgraph Backend
        direction TB
        VOID_BACKEND[ ]:::empty -->|AST + Mapping| IR
        IR["IR generator"] -->|IR optimized| ANALYZER["Analyzer"]
        ANALYZER -->|IR optimized + Metadata| INTERPRETER["Interpreter"]
        INTERPRETER -->|Raw output| BUILDER["Builder"]
    end
    Backend --> OUTPUT["Output"]
    %% Class definitions
    classDef empty width:0px,height:0px;
```


