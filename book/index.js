import hljs from "highlight.js/lib/core";

hljs.registerLanguage("kiwi", (hljs) => {
    const PACKAGE = {
        className: 'keyword',
        begin: 'package',
        illegal: '\\n|#|;',
        contains: [
            {
                className: 'title',
                begin: '\\s+[a-zA-Z0-9_\\.\\s*]*',
                contains: [
                    {
                        className: 'operator',
                        begin: '\\.',
                    }
                ]
            }
        ]
    }

    const IMPORT = {
        className: 'keyword',
        begin: 'import',
        illegal: '\\n|#|;',
        contains: [
            {
                className: 'title',
                begin: '\\s+[a-zA-Z0-9_\\.\\s*]*',
                contains: [
                    {
                        className: 'operator',
                        begin: '\\.',
                    }
                ]
            }
        ]
    }

    const KEYWORDS = [
        // Packages and Imports
        "package",
        "import",
        "as",

        // Types
        "\\$auto",
        "auto",

        // Control Flow
        "if",
        "else",
        "for",
        "while",
        "when",
        "do",
        "break",
        "continue",
        "return",
        "throw",
        "try",
        "catch",
        "finally",

        // Functions
        "fun",
        "operator",
        "infix",
        "inline",

        // Classes
        "class",
        "interface",
        "object",
        "enum",
        "data",

        // Modifiers
        "public",
        "private",
        "protected",
        "internal",
        "open",
        "final",
        "override",
        "abstract",
        "compiletime",
        "interpret",

        // Collections
        "in",
        "!in",
    ]

    const BUILTINS = [
        // Types
        "Int",
        "Float",
        "Bool",
        "String",
        "Char",
        "Nothing",

        // Functions
        "print",
        "log"
    ]

    const LITERALS = [
        "true",
        "false",
        "none"
    ]

    const OPERATORS = [
        // Arithmetic
        "+",
        "-",
        "*",
        "/",
        "%",

        // Comparison
        "==",
        "!=",
        "<",
        "<=",
        ">",
        ">=",

        // Logical
        "&&",
        "||",
        "!",

        // Assignment
        "=",
        "+=",
        "-=",
        "*=",
        "/=",
        "%=",

        // Arrow
        "->",
        "<-",

        // Increment/Decrement
        "++",
        "--",

        // Brackets
        "(",
        ")",
        "[",
        "]",
        "{",
        "}",

        // Other
        ".",
        ":",
        "?",
        "?:",
    ]

    const BACKSLASH_ESCAPE = {
        className: 'escape',
        begin: '\\\\[\\s\\S]',
        relevance: 0
    }

    const COMPILETIME_FORMATTED_INSERT = {
        className: 'operator',
        begin: '\\$\\{',
        end: '\\}',
        contains: []  // TODO: support nested
    }

    const FORMATTED_INSERT = {
        className: 'operator',
        begin: '\\{',
        end: '\\}',
        contains: []  // TODO: support nested
    }

    const APOS_STRING_MODE = {
        className: 'string',
        begin: "'",
        end: "'",
        illegal: '\\n',
        contains: [BACKSLASH_ESCAPE, COMPILETIME_FORMATTED_INSERT]
    }

    const FORMATTED_APOS_STRING_MODE = {
        className: 'string',
        begin: "f'",
        end: "'",
        illegal: '\\n',
        contains: [BACKSLASH_ESCAPE, FORMATTED_INSERT, COMPILETIME_FORMATTED_INSERT]
    }

    const APOS_DOC_STRING_MODE = {
        className: 'string',
        begin: "'''",
        end: "'''",
        contains: [BACKSLASH_ESCAPE, COMPILETIME_FORMATTED_INSERT]
    }

    const FORMATTED_APOS_DOC_STRING_MODE = {
        className: 'string',
        begin: "f'''",
        end: "'''",
        contains: [BACKSLASH_ESCAPE, FORMATTED_INSERT, COMPILETIME_FORMATTED_INSERT]
    }

    const QUOTE_STRING_MODE = {
        className: 'string',
        begin: '"',
        end: '"',
        illegal: '\\n',
        contains: [BACKSLASH_ESCAPE, COMPILETIME_FORMATTED_INSERT]
    }

    const FORMATTED_QUOTE_STRING_MODE = {
        className: 'string',
        begin: 'f"',
        end: '"',
        illegal: '\\n',
        contains: [BACKSLASH_ESCAPE, FORMATTED_INSERT, COMPILETIME_FORMATTED_INSERT]
    }

    const QUOTE_DOC_STRING_MODE = {
        className: 'string',
        begin: '"""',
        end: '"""',
        contains: [BACKSLASH_ESCAPE, COMPILETIME_FORMATTED_INSERT]
    }

    const FORMATTED_QUOTE_DOC_STRING_MODE = {
        className: 'string',
        begin: 'f"""',
        end: '"""',
        contains: [BACKSLASH_ESCAPE, FORMATTED_INSERT, COMPILETIME_FORMATTED_INSERT]
    }

    const COMPILETIME_ANYTHING = {
        className: 'variable.language',
        begin: '\\$(?!auto)[a-zA-Z_][a-zA-Z0-9_]*',
    }

    const ELLIPSIS = {
        className: 'comment',
        begin: '\\.{3}',
    }

    return {
        name: 'Kiwi',
        case_insensitive: true, // language is case-insensitive
        keywords: {
            keyword: KEYWORDS.join(" "),
            built_in: BUILTINS.join(" "),
            literal: LITERALS.join(" "),
            operator: OPERATORS.join(" "),
        },
        contains: [
            PACKAGE,
            IMPORT,
            APOS_STRING_MODE,               // 'string'
            FORMATTED_APOS_STRING_MODE,     // f'string'
            APOS_DOC_STRING_MODE,           // '''string'''
            FORMATTED_APOS_DOC_STRING_MODE, // f'''string'''
            QUOTE_STRING_MODE,              // "string"
            FORMATTED_QUOTE_STRING_MODE,    // f"string"
            QUOTE_DOC_STRING_MODE,          // """string"""
            FORMATTED_QUOTE_DOC_STRING_MODE,// f"""string"""
            hljs.C_NUMBER_MODE,             // 0x..., 0..., decimal, float
            hljs.BINARY_NUMBER_MODE,        // 0b...
            hljs.HASH_COMMENT_MODE,         // #...
            COMPILETIME_ANYTHING,           // $...
            ELLIPSIS,                       // ...
        ]
    }
});

window.addEventListener("load", (event) => {
    document
        .querySelectorAll("code.language-kiwi")
        .forEach((block) => hljs.highlightBlock(block));
});