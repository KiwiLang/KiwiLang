import hljs from "highlight.js/lib/core";

hljs.registerLanguage("kiwi", (hljs) => {
    const KEYWORDS = [
        // Packages and Imports
        "package",
        "import",
        "as",

        // Control Flow
        "trigger",
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
        "extend",
        "by",
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

    const EXTENSIONS = [
        // Methods
        "get",
        "set",
        "forEach",
        "map",
        "filter",
        "reduce",
        "fold",
        "any",
        "all",
        "isEmpty",
        "isNotEmpty",
        "contains",
        "containsAll",
        "indexOf",
        "lastIndexOf",
        "toConstant",

        // Operators
        "step",
        "until",
        "downTo",
        "rangeTo",

        // Properties
        "size",
        "length",
        "first",
        "last",
        "keys",
        "values",
        "indices",
        "MAX_VALUE",
        "MIN_VALUE",
    ]

    const PACKAGE_NAME = {
        className: 'package-name',
        begin: '(?<=package)[ ]+[a-zA-Z0-9_.]*(?!(\\n|#|as))',
    }

    const IMPORT_PACKAGE_NAME = {
        className: 'package-name',
        begin: '(?<=import)[ ]+[a-zA-Z0-9_.*]*(?!(\\n|#|as))',
    }

    const TEMPLATE_TYPE = {
        className: 'type',
        begin: '[a-zA-Z_][a-zA-Z0-9_]*',
    }

    const TEMPLATE_COMPILETIME_TYPE = {
        className: 'compiletime-type',
        begin: '\\$[a-zA-Z_][a-zA-Z0-9_]*',
    }

    const TEMPLATE_TYPES = {
        className: 'wrapper',
        begin: '(?<![ ])\\<(?!\\-)',
        end: '\\>',
        illegal: '[[](){}-+;?|&@#~!=/*^%:]',
        contains: [TEMPLATE_TYPE, TEMPLATE_COMPILETIME_TYPE, hljs.HASH_COMMENT_MODE]
    }

    const BACKSLASH_ESCAPE = {
        className: 'escape',
        begin: '\\\\[\\s\\S]',
        relevance: 0
    }

    const RESET = {
        className: 'reset',
        begin: '[^}]',
    }

    const COMPILETIME_FORMATTED_INSERT = {
        className: 'format',
        begin: '\\$\\{',
        end: '\\}',
        contains: [RESET]
    }

    const BINARY_NUMBER = {
        className: 'number',
        begin: '\\b0b[01]+',
    }

    const OCTAL_NUMBER = {
        className: 'number',
        begin: '\\b0o[0-7]+',
    }

    const HEX_NUMBER = {
        className: 'number',
        begin: '\\b0x[0-9a-fA-F]+',
    }

    const DECIMAL_NUMBER = {
        className: 'number',
        begin: '\\b[0-9]+',
    }

    const FLOAT_NUMBER = {
        className: 'number',
        begin: '\\b[0-9]+\\.[0-9]*(e[+-]?\\d+)?(?![ ]*\\.)|' +
            '\\b(?<!\\.)\\.[0-9]+(e[+-]?\\d+)?|' +
            '\\b[0-9]+e[+-]?\\d+'
    }

    const FORMATTED_INSERT = {
        className: 'format',
        begin: '\\{',
        end: '\\}',
        contains: [RESET, hljs.HASH_COMMENT_MODE]
    }

    const INSERT_START = {
        className: 'format',
        begin: '\\$',
    }

    const INSERT_CONTENT = {
        className: 'reset',
        begin: '(?<=\\$)[a-zA-Z_][a-zA-Z0-9_]*',
    }

    const APOS_STRING_MODE = {
        className: 'string',
        begin: "'",
        end: "'",
        illegal: '\\n',
        contains: [
            BACKSLASH_ESCAPE,
            COMPILETIME_FORMATTED_INSERT,
            INSERT_START,
            INSERT_CONTENT,
        ]
    }

    const FORMATTED_APOS_STRING_MODE = {
        className: 'string',
        begin: "f'",
        end: "'",
        illegal: '\\n',
        contains: [
            BACKSLASH_ESCAPE,
            FORMATTED_INSERT,
            COMPILETIME_FORMATTED_INSERT,
            INSERT_START,
            INSERT_CONTENT,
        ]
    }

    const APOS_DOC_STRING_MODE = {
        className: 'string',
        begin: "'''",
        end: "'''",
        contains: [
            BACKSLASH_ESCAPE,
            COMPILETIME_FORMATTED_INSERT,
            INSERT_START,
            INSERT_CONTENT,
        ]
    }

    const FORMATTED_APOS_DOC_STRING_MODE = {
        className: 'string',
        begin: "f'''",
        end: "'''",
        contains: [
            BACKSLASH_ESCAPE,
            FORMATTED_INSERT,
            COMPILETIME_FORMATTED_INSERT,
            INSERT_START,
            INSERT_CONTENT,
        ]
    }

    const QUOTE_STRING_MODE = {
        className: 'string',
        begin: '"',
        end: '"',
        illegal: '\\n',
        contains: [
            BACKSLASH_ESCAPE,
            COMPILETIME_FORMATTED_INSERT,
            INSERT_START,
            INSERT_CONTENT,
        ]
    }

    const FORMATTED_QUOTE_STRING_MODE = {
        className: 'string',
        begin: 'f"',
        end: '"',
        illegal: '\\n',
        contains: [
            BACKSLASH_ESCAPE,
            FORMATTED_INSERT,
            COMPILETIME_FORMATTED_INSERT,
            INSERT_START,
            INSERT_CONTENT,
        ]
    }

    const QUOTE_DOC_STRING_MODE = {
        className: 'string',
        begin: '"""',
        end: '"""',
        contains: [
            BACKSLASH_ESCAPE,
            COMPILETIME_FORMATTED_INSERT,
            INSERT_START,
            INSERT_CONTENT,
        ]
    }

    const FORMATTED_QUOTE_DOC_STRING_MODE = {
        className: 'string',
        begin: 'f"""',
        end: '"""',
        contains: [
            BACKSLASH_ESCAPE,
            FORMATTED_INSERT,
            COMPILETIME_FORMATTED_INSERT,
            INSERT_START,
            INSERT_CONTENT,
        ]
    }

    const TYPE = {
        className: 'type',
        begin: '(?<=(\\S:|->))[ ]*(?!auto)[a-zA-Z_][a-zA-Z0-9_]*(?!\\w*\\s*\\.)',
    }

    const COMPILETIME_TYPE = {
        className: 'compiletime-type',
        begin: '(?<=(\\S:|->))[ ]*(?!\\$auto)\\$[a-zA-Z_][a-zA-Z0-9_]*(?!\\w*\\s*\\.)',     
    }

    const AUTO_TYPE = {
        className: 'keyword',
        begin: '((?!\\b)\\$a|\\ba)uto\\b',
    }

    const PARAMETER_DELEGATION = {
        className: 'parameter-delegation',
        begin: '(?<=by)[ ]+[a-zA-Z_][a-zA-Z0-9_]*',
    }

    const COMPILETIME_CALL = {
        className: 'compiletime-call',
        begin: '\\$[a-zA-Z_][a-zA-Z0-9_]*\\b',
    }

    const ELLIPSIS = {
        className: 'ellipsis',
        begin: '(?<=\\s)\\.{3}',
    }

    const SELECTOR = {
        className: 'selector',
        begin: '@[a-zA-Z_][a-zA-Z0-9_]*',
    }

    const EVENT = {
        className: 'event',
        begin: '(?<=<-)[ ]*[a-zA-Z_][a-zA-Z0-9_]*',
    }

    return {
        name: 'Kiwi',
        case_insensitive: true, // language is case-insensitive
        keywords: {
            keyword: KEYWORDS.join(" "),
            builtin: BUILTINS.join(" "),
            literal: LITERALS.join(" "),
            operator: OPERATORS.join(" "),
            extension: EXTENSIONS.join(" "),
        },
        contains: [
            hljs.HASH_COMMENT_MODE,         // # single line comment
            PACKAGE_NAME,                   // package <package>
            IMPORT_PACKAGE_NAME,            // import <package> as <name>
            TEMPLATE_TYPES,                 // <type, type, ...>
            APOS_STRING_MODE,               // 'string'
            FORMATTED_APOS_STRING_MODE,     // f'string'
            APOS_DOC_STRING_MODE,           // '''string'''
            FORMATTED_APOS_DOC_STRING_MODE, // f'''string'''
            QUOTE_STRING_MODE,              // "string"
            FORMATTED_QUOTE_STRING_MODE,    // f"string"
            QUOTE_DOC_STRING_MODE,          // """string"""
            FORMATTED_QUOTE_DOC_STRING_MODE,// f"""string"""
            BINARY_NUMBER,                  // 0b0101
            OCTAL_NUMBER,                   // 0o174
            HEX_NUMBER,                     // 0x4F0
            DECIMAL_NUMBER,                 // 123
            FLOAT_NUMBER,                   // 123.456, .456, 123e-4
            COMPILETIME_TYPE,               // : $<type>
            TYPE,                           // : <type>
            AUTO_TYPE,                      // : auto
            PARAMETER_DELEGATION,           // by <name>
            COMPILETIME_CALL,               // $<name>
            ELLIPSIS,                       // ...
            SELECTOR,                       // @<name>
            EVENT,                          // <- <name>
        ]
    }
});

window.addEventListener("load", (_) => {
    document
        .querySelectorAll("code.language-kiwi")
        .forEach((block) => hljs.highlightBlock(block));
});