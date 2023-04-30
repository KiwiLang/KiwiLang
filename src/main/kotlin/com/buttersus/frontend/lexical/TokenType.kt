package com.buttersus.frontend.lexical

enum class TokenType {
    NUMBER,
    STRING,
    DOCSTRING,
    IDENTIFIER,
    CONST_IDENTIFIER,
    RESERVED,
    KEYWORD,
    NEWLINE,
    EOF,

    // Special tokens
    PACKAGE_NAME,
    IMPORT_PACKAGE_NAME,
}