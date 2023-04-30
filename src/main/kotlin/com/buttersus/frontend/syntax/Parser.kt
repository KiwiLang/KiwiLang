package com.buttersus.frontend.syntax

import com.buttersus.frontend.lexical.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.reflect.KFunction0

class Parser {
    // PARSER CONTEXT
    // ==============

    private lateinit var lexer: Lexer
    private lateinit var lexerStream: Iterator<TokenInfo>
    private lateinit var source: String
    private lateinit var filePath: String
    private var index = 0

    fun link(lexer: Lexer) {
        this.lexer = lexer
    }

    // PARSER METHODS
    // ==============

    fun load(source: String, filePath: String = "terminal"): Parser = this.also {
        this.filePath = filePath
        this.source = source
        lexerStream = lexer.tokens()
        index = 0
    }

    private fun raise(message: String, expected: String): Nothing {
        val token = peek().token
        val lines = source.lines()
        val offset = floor(log10(token.line + 1.0)).toInt() + 1
        val size = token.lexeme.length
        // Here we add +1 to line, because index is located right before the token.
        throw SyntaxError(
            """
error: expected `$expected`, found `${token.lexeme}`
--> $filePath:${token.line + 1}:${token.column + 1}
|
| ${
                arrayOf(3, 2, 1, 0).mapNotNull {
                    if (token.line - it >= 0) "${
                        (token.line - it + 1).toString().padStart(offset)
                    }. ${
                        lines[token.line - it]
                    }" else null
                }.joinToString("\n| ")
            }
| ${" ".repeat(token.column + offset + 1)} ${"^".repeat(size)} $message
|
| ------ syntax error
error: could not compile `$filePath` due to previous error
            """.trimIndent()
        )
    }

    private val lexemes = arrayListOf<TokenInfo>()
    private fun mark(): Int = index
    private fun reset(index: Int) {
        this.index = index
    }

    private fun next() = peek().also { index++ }
    private fun peek(): TokenAST.TokenWrapper {
        while (lexemes.size <= index)
            lexemes.add(lexerStream.next())
        return TokenAST.TokenWrapper(lexemes[index])
    }

    private val memories =
        mutableMapOf<Int, MutableMap<() -> TokenAST?, Pair<TokenAST?, Int>>>()

    private fun memoize(
        function: KFunction0<TokenAST?>,
        code: () -> TokenAST?
    ): TokenAST? {
        val position = mark()
        val memory = memories.getOrPut(position) { mutableMapOf() }
        memory[function]?.also {
            reset(it.second)
            return it.first
        }
        return code().also {
            memory[function] = it to mark()
        }
    }

    private fun memoizeLR(
        function: KFunction0<TokenAST?>,
        code: () -> TokenAST?
    ): TokenAST? {
        val position = mark()
        val memory = memories.getOrPut(position) { mutableMapOf() }
        memory[function]?.also {
            reset(it.second)
            return it.first
        }
        var lastResult: TokenAST? = null
        var lastPosition = position
        memory[function] = null to lastPosition
        while (true) {
            reset(position)
            val result = code()
            val newPosition = mark()
            if (newPosition <= lastPosition) break
            lastResult = result
            lastPosition = newPosition
            memory[function] = lastResult to lastPosition
        }
        return lastResult.also {
            reset(lastPosition)
        }
    }


    /** ?=LOOKAHEAD */
    @Suppress("SameParameterValue")
    private fun lookahead(lookahead: () -> TokenAST?): TokenAST? {
        val mark = mark()
        val result = lookahead()
        reset(mark)
        return result
    }

    /** { OPTIONAL1 OPTIONAL2}? */
    @Suppress("SameParameterValue")
    private fun optional(vararg options: () -> TokenAST?): Unit? {
        val mark = mark()
        options.map { it() ?: return null.also { reset(mark) } }.toTypedArray()
        return Unit
    }

    /** <TYPE> */
    @Suppress("SameParameterValue")
    private fun match(type: TokenType): TokenAST? {
        val token = peek().token
        if (token.type == TokenType.KEYWORD) raise(
            "wrong identifier name (reserved keyword)",
            "identifier"
        )
        return if (token.type == type) next() else null
    }

    /** 'LEXEME' */
    @Suppress("SameParameterValue")
    private fun match(lexeme: String) =
        if (peek().token.lexeme == lexeme) next() else null

    /** { CONDITION1 | CONDITION2 } */
    @Suppress("SameParameterValue")
    private fun alternativeSingle(vararg conditions: () -> TokenAST?) =
        conditions.map { it() }.firstOrNull { it != null }

    /** { CONDITION1 ... | CONDITION2 ... } */
    @Suppress("SameParameterValue")
    private fun alternativeMulti(vararg conditions: () -> Boolean): Unit? {
        val mark = mark()
        for (condition in conditions) {
            if (condition()) return Unit
            reset(mark)
        }
        return null
    }

    /** SEPARATOR:VALUE+ */
    @Suppress("SameParameterValue")
    private fun separator(value: () -> TokenAST?, separator: () -> TokenAST?,
                          from: Int = 1, to: Int = Int.MAX_VALUE): TokenAST.List? {
        val list = TokenAST.List()
        var mark = mark()
        value()?.let { list.add(it) } ?: return (if (list.size < from) null.also {
            reset(mark)
        } else list)
        while (list.size < to) {
            mark = mark()
            separator() ?: run {
                reset(mark)
                return (if (list.size < from) null.also {
                    reset(mark)
                } else list)
            }; value()?.let { list.add(it) } ?: run {
                reset(mark)
                return (if (list.size < from) null.also {
                    reset(mark)
                } else list)
            }
        }
        return list
    }

    /** VALUE+ */
    @Suppress("SameParameterValue")
    private fun list(from: Int = 1, to: Int = Int.MAX_VALUE, value: () -> TokenAST?): TokenAST.List? {
        val list = TokenAST.List()
        while (list.size < to) {
            val mark = mark()
            value()?.let { list.add(it) } ?: run {
                reset(mark)
                if (list.size < from) return null
                return list
            }
        }
        return list
    }

    // GRAMMAR SPECIFICATION
    // =====================

    fun parse(): TokenAST {
        // start: .package? .{<NEWLINE>|';'}:import* statement*
        // <NEWLINE>? !<EOF> -> "unexpected context"
        // => Module(package, imports, statements)
        val `package` = `package`() ?: TokenAST.Void
        val imports = separator(
            { import() },
            {
                alternativeSingle(
                    { match(TokenType.NEWLINE) },
                    { match(";") }
                )
            }
        ) ?: TokenAST.List()
        val statements = list { statement() } ?: TokenAST.List()
        match(TokenType.NEWLINE)
        match(TokenType.EOF) ?: raise("unexpected", "EOF")
        return TokenAST.Module(`package`, imports, statements)
    }

    private fun `package`(): TokenAST? {
        val mark = mark()

        // 'package' .!<PACKAGE_NAME>[enable] -> "wrong package name"
        // !{<NEWLINE> | ';'} -> "expected separator" / "';' or newline"
        // => Package(identifier)
        fun case0(): TokenAST? {
            match("package") ?: return null
            lexer.enable(TokenType.PACKAGE_NAME)
            val identifier = match(TokenType.PACKAGE_NAME) ?: raise("wrong package name", "PACKAGE_NAME")
            alternativeSingle(
                { match(TokenType.NEWLINE) },
                { match(";") }
            ) ?: raise("expected separator", "';' or newline")
            return TokenAST.Package(identifier)
        }

        case0()?.also { return it }
        reset(mark)

        return null
    }

    private fun import(): TokenAST? {
        val mark = mark()

        // 'import' .!<PACKAGE_NAME>[enable] -> "Expected package name"
        // !{<NEWLINE> | ';'} -> "Expected ';' or newline"
        // => Import(packageName)
        fun case0(): TokenAST? {
            match("import") ?: return null
            lexer.enable(TokenType.IMPORT_PACKAGE_NAME)
            val packageName = match(TokenType.IMPORT_PACKAGE_NAME) ?: raise("wrong package name", "IMPORT_PACKAGE_NAME")
            alternativeSingle(
                { match(TokenType.NEWLINE) },
                { match(";") }
            ) ?: raise("expected separator", "';' or newline")
            return TokenAST.Import(packageName)
        }

        case0()?.also { return it }
        reset(mark)

        return null
    }

    private fun statement(): TokenAST? = memoize(::statement) {
        val mark = mark()

        // =.compoundStatement <NEWLINE>?
        fun case0(): TokenAST? {
            val first = compoundStatement() ?: return null
            match(TokenType.NEWLINE)
            return first
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // =.expressionStatement ?='}'
        fun case1(): TokenAST? {
            val first = expressionStatement() ?: return null
            lookahead { match("}") } ?: return null
            return first
        }

        case1()?.also { return@memoize it }
        reset(mark)

        // =.expressionStatement !{<NEWLINE> | ';'} -> "expected ';' or newline" / "<NEWLINE> or ';'"
        fun case2(): TokenAST? {
            val first = expressionStatement() ?: return null
            alternativeSingle(
                { match(TokenType.NEWLINE) },
                { match(";") }
            ) ?: raise("expected separator", "<NEWLINE> or ';'")
            return first
        }

        case2()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    // COMPOUND STATEMENTS
    // ===================

    private fun compoundStatement(): TokenAST? = memoize(::compoundStatement) {
        val mark = mark()

        // ={ifStatement | whileStatement | forStatement}
        alternativeSingle(
            { ifStatement() },
            { whileStatement() },
            { forStatement() }
        )?.also { return@memoize it }

        // ={functionDeclaration | TODO: classDeclaration | TODO: interfaceDeclaration}
        alternativeSingle(
            { functionDeclaration() },
            { null },
            { null }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    // TODO: Добавить опциональные скобки
    private fun ifStatement(): TokenAST? = memoize(::ifStatement) {
        val mark = mark()

        // 'if' .!expression -> "wrong condition" '->'?
        // !'{' -> "missing opening brace" .statement* !'}' -> "missing closing brace"
        // {'else' { .statement | '{' .statement* !'}' -> "missing closing brace"}}?
        // => IfStatement(condition, body, elseBody)
        fun case0(): TokenAST? {
            match("if") ?: return null
            val condition = expression() ?: raise("wrong condition", "expression")
            match("->")
            match("{") ?: raise("missing opening brace", "'{'")
            val body = list { statement() } ?: TokenAST.List()
            match("}") ?: raise("missing closing brace", "'}'")
            var elseBody: TokenAST = TokenAST.Void
            alternativeMulti(
                {
                    match("else") ?: return@alternativeMulti false
                    true
                },
                {
                    match("{") ?: return@alternativeMulti false
                    val first = list { statement() } ?: TokenAST.List()
                    match("}") ?: raise("missing closing brace", "'}'")
                    elseBody = first
                    true
                }
            ) ?: run {
                elseBody = TokenAST.Void
            }
            return TokenAST.IfStatement(condition, body, elseBody)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    // TODO: Добавить опциональные скобки
    private fun whileStatement(): TokenAST? = memoize(::whileStatement) {
        val mark = mark()

        // 'while' .!expression -> "wrong condition" '->'?
        // !'{' -> "missing opening brace" .statement* !'}' -> "missing closing brace"
        // => WhileStatement(condition, body)
        fun case0(): TokenAST? {
            match("while") ?: return null
            val condition = expression() ?: raise("wrong condition", "expression")
            match("->")
            match("{") ?: raise("missing opening brace", "'{'")
            val body = list { statement() } ?: TokenAST.List()
            match("}") ?: raise("missing closing brace", "'}'")
            return TokenAST.WhileStatement(condition, body)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    // TODO: Добавить опциональные скобки
    private fun forStatement(): TokenAST? = memoize(::forStatement) {
        val mark = mark()

        // 'for' .<IDENTIFIER> 'in' .!expression -> "wrong iterable" '->'?
        // !'{' -> "missing opening brace" .statement* !'}' -> "missing closing brace"
        // => ForInStatement(variable, iterable, body)
        fun case0(): TokenAST? {
            match("for") ?: return null
            val variable = match(TokenType.IDENTIFIER) ?: raise("wrong variable name", "IDENTIFIER")
            match("in") ?: return null
            val iterable = expression() ?: raise("wrong iterable", "expression")
            match("->")
            match("{") ?: raise("missing opening brace", "'{'")
            val body = list { statement() } ?: TokenAST.List()
            match("}") ?: raise("missing closing brace", "'}'")
            return TokenAST.ForInStatement(variable, iterable, body)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun functionDeclaration(): TokenAST? = memoize(::functionDeclaration) {
        val mark = mark()

        // 'fun' .!<IDENTIFIER> -> "wrong function name"
        // {'(' .{2}parameters?[=TokenAST.List(TokenAST.List(), TokenAST.List())] !')' -> "missing closing bracket"}?
        // {'->' .!type -> "wrong return type"}?
        // !{'{' .statement* !'}' -> "missing closing brace"
        // | '=' .!expression -> "wrong expression"} -> "wrong function body" / "statement or expression"
        // => FunctionDeclaration(name, parameters, assignedParameters, returns, body)
        fun case0(): TokenAST? {
            match("fun") ?: return null
            val name = match(TokenType.IDENTIFIER) ?: raise("wrong function name", "IDENTIFIER")
            var second: TokenAST.List = TokenAST.List(TokenAST.List(), TokenAST.List())
            optional(
                { match("(") },
                { parameters()?.also { second = it } ?: TokenAST.Void },
                { match(")") ?: raise("missing closing bracket", "')'") }
            ) ?: run {
                second = TokenAST.List(TokenAST.List(), TokenAST.List())
            }
            var returns: TokenAST = TokenAST.Void
            optional(
                { match("->") },
                { type()?.also { returns = it } }
            ) ?: run {
                returns = TokenAST.Void
            }
            var body: TokenAST = TokenAST.Void
            alternativeMulti(
                {
                    match("{") ?: return@alternativeMulti false
                    body = list { statement() } ?: TokenAST.List()
                    match("}") ?: raise("missing closing brace", "'}'")
                    true
                },
                {
                    match("=") ?: return@alternativeMulti false
                    body = expression() ?: raise("wrong expression", "expression")
                    true
                }
            ) ?: raise("wrong function body", "statement or expression")
            return TokenAST.FunctionDeclaration(name, second[0], second[1], returns, body)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun parameters(): TokenAST.List? = memoize(::parameters) {
        val mark = mark()

        // .',':{=parameter ?!'='}+
        // {',' .',':assignedParameter+}? ','? !?=')' -> "missing closing bracket"
        fun case0(): TokenAST? {
            val first = separator(
                {
                    val first = parameter()
                    lookahead { match("=") }?.also { return@separator null }
                    first
                },
                { match(",") }
            ) ?: return null
            var second: TokenAST.List = TokenAST.List()
            optional(
                { match(",") },
                {
                    separator(
                        { assignedParameter() },
                        { match(",") }
                    )?.also { second = it }
                }
            ) ?: run {
                second = TokenAST.List()
            }
            match(",") ?: TokenAST.Void
            lookahead { match(")") } ?: raise("missing closing bracket", "')'")
            return TokenAST.List(first, second)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // =.={TokenAST.List()}.',':assignedParameter+ ','? !?=')' -> "missing closing bracket"
        fun case1(): TokenAST? {
            val first = TokenAST.List()
            val second = separator(
                { assignedParameter() },
                { match(",") }
            ) ?: return null
            match(",") ?: TokenAST.Void
            lookahead { match(")") } ?: raise("missing closing bracket", "')'")
            return TokenAST.List(first, second)
        }

        case1()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    } as TokenAST.List?

    private fun assignedParameter(): TokenAST? = memoize(::assignedParameter) {
        val mark = mark()

        // .<IDENTIFIER> ':' .!type -> "wrong data type"
        // '=' .!expression -> "wrong expression"
        // => AssignedParameter(name, type, value)
        fun case0(): TokenAST? {
            val name = match(TokenType.IDENTIFIER) ?: return null
            match(":") ?: return null
            val type = type() ?: raise("wrong data type", "type")
            match("=") ?: return null
            val value = expression() ?: raise("wrong expression", "expression")
            return TokenAST.AssignedParameter(name, type, value)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun parameter(): TokenAST? = memoize(::parameter) {
        val mark = mark()

        // .<IDENTIFIER> ':' .!type -> "wrong data type"
        // => Parameter(name, type)
        fun case0(): TokenAST? {
            val name = match(TokenType.IDENTIFIER) ?: return null
            match(":") ?: return null
            val type = type() ?: raise("wrong data type", "type")
            return TokenAST.Parameter(name, type)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    // EXPRESSION STATEMENTS
    // =====================

    private fun expressionStatement(): TokenAST? = memoize(::expressionStatement) {
        val mark = mark()

        // ={returnStatement | primitiveStatement | variableDeclaration | expression}
        alternativeSingle(
            { returnStatement() },
            { primitiveStatement() },
            { variableDeclaration() },
            { expression() }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun variableDeclaration(): TokenAST? = memoize(::variableDeclaration) {
        val mark = mark()

        // .<IDENTIFIER> ':' .!type -> "wrong data type"
        // {'=' .!expression -> "wrong expression"}?
        // => VariableDeclaration(name, type, value)
        fun case0(): TokenAST? {
            val name = match(TokenType.IDENTIFIER) ?: return null
            match(":") ?: return null
            val type = type() ?: raise("wrong data type", "type")
            var value: TokenAST = TokenAST.Void
            optional(
                { match("=") },
                { expression()?.also { value = it } ?: raise("wrong expression", "expression") }
            ) ?: run {
                value = TokenAST.Void
            }
            return TokenAST.VariableDeclaration(name, type, value)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun type(): TokenAST? = memoize(::type) {
        val mark = mark()

        // ={"auto"[=TokenAST.AutoType] | "$auto"[=TokenAST.CompileTimeAutoType] | expression}
        alternativeSingle(
            { match("auto")?.let { TokenAST.AutoType } },
            { match("\$auto")?.let { TokenAST.CompileTimeAutoType } },
            { expression() }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }


    private fun returnStatement(): TokenAST? = memoize(::returnStatement) {
        val mark = mark()

        // "return" .!expression -> "wrong expression"
        // => ReturnStatement(value)
        fun case0(): TokenAST? {
            match("return") ?: return null
            val value = expression() ?: raise("wrong expression", "expression")
            return TokenAST.ReturnStatement(value)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun primitiveStatement(): TokenAST? = memoize(::primitiveStatement) {
        val mark = mark()

        // ={ 'break'[=TokenAST.BreakStatement]
        // | 'continue'[=TokenAST.ContinueStatement]
        // | 'pass'[=TokenAST.PassStatement]
        // }
        alternativeSingle(
            { match("break")?.let { TokenAST.BreakStatement } },
            { match("continue")?.let { TokenAST.ContinueStatement } },
            { match("pass")?.let { TokenAST.PassStatement } }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    // EXPRESSIONS
    // ===========

    // TODO: Добавить операторы сравнения и диапазонов
    // TODO: Добавить больше видов литералов (словари, множества, кортежи, ...)
    private fun expression(): TokenAST? = memoize(::expression) {
        val mark = mark()

        // '[' .!',':expression* ','? !']' -> "missing closing bracket"
        // => ListExpression(items)
        fun case0(): TokenAST? {
            match("[") ?: return null
            val items = separator(
                { expression() },
                { match(",") }
            ) ?: TokenAST.List()
            match(",")
            match("]") ?: raise("missing closing bracket", "']'")
            return TokenAST.ListExpression(items)
        }

        case0()?.also { return@memoize it }

        // =sum
        sum()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun sum(): TokenAST? = memoizeLR(::sum) {
        val mark = mark()

        // .sum '+' .!term -> "wrong expression" => Add(left, right)
        fun case0(): TokenAST? {
            val left = sum() ?: return null
            match("+") ?: return null
            val right = term() ?: raise("wrong expression", "term")
            return TokenAST.Add(left, right)
        }

        case0()?.also { return@memoizeLR it }
        reset(mark)

        // .sum '-' .!term -> "wrong expression" => Sub(left, right)
        fun case1(): TokenAST? {
            val left = sum() ?: return null
            match("-") ?: return null
            val right = term() ?: raise("wrong expression", "term")
            return TokenAST.Sub(left, right)
        }

        case1()?.also { return@memoizeLR it }
        reset(mark)

        // =term
        term()?.also { return@memoizeLR it }
        reset(mark)

        return@memoizeLR null
    }

    private fun term(): TokenAST? = memoizeLR(::term) {
        val mark = mark()

        // .term '*' .!factor -> "wrong expression" => Mul(left, right)
        fun case0(): TokenAST? {
            val left = term() ?: return null
            match("*") ?: return null
            val right = factor() ?: raise("wrong expression", "factor")
            return TokenAST.Mul(left, right)
        }

        case0()?.also { return@memoizeLR it }
        reset(mark)

        // .term '/' .!factor -> "wrong expression" => Div(left, right)
        fun case1(): TokenAST? {
            val left = term() ?: return null
            match("/") ?: return null
            val right = factor() ?: raise("wrong expression", "factor")
            return TokenAST.Div(left, right)
        }

        case1()?.also { return@memoizeLR it }
        reset(mark)

        // .term '%' .!factor -> "wrong expression" => Mod(left, right)
        fun case2(): TokenAST? {
            val left = term() ?: return null
            match("%") ?: return null
            val right = factor() ?: raise("wrong expression", "factor")
            return TokenAST.Mod(left, right)
        }

        case2()?.also { return@memoizeLR it }
        reset(mark)

        // =factor
        factor()?.also { return@memoizeLR it }
        reset(mark)

        return@memoizeLR null
    }

    private fun factor(): TokenAST? = memoize(::factor) {
        val mark = mark()

        // ='+' .!primary -> "wrong expression" => Pos(value)
        fun case0(): TokenAST? {
            match("+") ?: return null
            val value = factor() ?: raise("wrong expression", "factor")
            return TokenAST.Pos(value)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // ='-' .!primary -> "wrong expression" => Neg(value)
        fun case1(): TokenAST? {
            match("-") ?: return null
            val value = factor() ?: raise("wrong expression", "factor")
            return TokenAST.Neg(value)
        }

        case1()?.also { return@memoize it }
        reset(mark)

        // =primary
        primary()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun primary(): TokenAST? = memoizeLR(::primary) {
        val mark = mark()

        // .primary '.' !.atom -> "wrong attribute" => Attribute(parent, attribute)
        fun case0(): TokenAST? {
            val parent = primary() ?: return null
            match(".") ?: return null
            val attribute = atom() ?: raise("wrong attribute", "atom")
            return TokenAST.Attribute(parent, attribute)
        }

        case0()?.also { return@memoizeLR it }
        reset(mark)

        // .primary {'<' .',':expression* ','? !'>' -> "missing closing angle bracket"}?
        // '(' .{2}arguments?[=TokenAST.List(TokenAST.List(), TokenAST.List())]
        // !')' -> "missing closing bracket"
        // => Call(function, generics, arguments, namedArguments)
        fun case1(): TokenAST? {
            val function = primary() ?: return null
            var generics: TokenAST.List = TokenAST.List()
            optional(
                { match("<") },
                { separator(
                    { expression() },
                    { match(",") }
                )?.also { generics = TokenAST.List(it) } },
                { match("?") ?: TokenAST.Void },
                { match(">") ?: raise("missing closing angle bracket", "'>'") }
            ) ?: run {
                generics = TokenAST.List()
            }
            match("(") ?: return null
            val third = arguments() ?: TokenAST.List(TokenAST.List(), TokenAST.List())
            match(")") ?: raise("missing closing bracket", "')'")
            return TokenAST.Call(function, generics, third[0], third[1])
        }

        case1()?.also { return@memoizeLR it }
        reset(mark)

        // .primary '<' .',':expression* ','? !'>' -> "missing closing angle bracket"
        // => Generic(parent, generics)
        fun case2(): TokenAST? {
            val parent = primary() ?: return null
            match("<") ?: return null
            val generics = separator(
                { expression() },
                { match(",") }
            ) ?: raise("missing closing angle bracket", "'>'")
            match("?")
            match(">") ?: raise("missing closing angle bracket", "'>'")
            return TokenAST.Generic(parent, generics)
        }

        case2()?.also { return@memoizeLR it }
        reset(mark)

        // .primary '[' .':':{!expression -> "wrong expression"}* !']' -> "missing closing square bracket"
        // => Index(parent, indices)
        fun case3(): TokenAST? {
            val parent = primary() ?: return null
            match("[") ?: return null
            val indices = separator(
                { expression() ?: raise("wrong expression", "expression") },
                { match(":") }
            ) ?: raise("missing closing square bracket", "']'")
            match("]") ?: raise("missing closing square bracket", "']'")
            return TokenAST.Index(parent, indices)
        }

        case3()?.also { return@memoizeLR it }
        reset(mark)

        // =atom
        atom()?.also { return@memoizeLR it }
        reset(mark)

        return@memoizeLR null
    }

    private fun arguments(): TokenAST.List? = memoize(::arguments) {
        val mark = mark()

        // .',':{=expression ?!'='}+
        // {',' .',':assignedArgument+}? ','? !?=')' -> "missing closing bracket"
        fun case0(): TokenAST? {
            val first = separator(
                {
                    val first = expression()
                    lookahead { match("=") }?.also { return@separator null }
                    first
                },
                { match(",") }
            ) ?: return null
            var second: TokenAST.List = TokenAST.List()
            optional(
                { match(",") },
                {
                    separator(
                        { assignedArgument() },
                        { match(",") }
                    )?.also { second = it }
                }
            ) ?: run {
                second = TokenAST.List()
            }
            match(",") ?: TokenAST.Void
            lookahead { match(")") } ?: raise("missing closing bracket", "')'")
            return TokenAST.List(first, second)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // =.={TokenAST.List()}.',':assignedArgument+ ','? !?=')' -> "missing closing bracket"
        fun case1(): TokenAST? {
            val first = TokenAST.List()
            val second = separator(
                { assignedArgument() },
                { match(",") }
            ) ?: return null
            match(",") ?: TokenAST.Void
            lookahead { match(")") } ?: raise("missing closing bracket", "')'")
            return TokenAST.List(first, second)
        }

        case1()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    } as TokenAST.List?

    private fun assignedArgument(): TokenAST? = memoize(::assignedArgument) {
        val mark = mark()

        // .<IDENTIFIER> '=' .!expression -> "wrong expression"
        // => AssignedArgument(name, value)
        fun case0(): TokenAST? {
            val name = match(TokenType.IDENTIFIER) ?: return null
            match("=") ?: return null
            val value = expression() ?: raise("wrong expression", "expression")
            return TokenAST.AssignedArgument(name, value)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun atom(): TokenAST? = memoize(::atom) {
        val mark = mark()

        // ='true'
        match("true")?.also {
            return@memoize TokenAST.Bool.True
        }
        reset(mark)

        // ='false'
        match("false")?.also {
            return@memoize TokenAST.Bool.False
        }
        reset(mark)

        // ={<IDENTIFIER> | <NUMBER> | <STRING> | <CONST_IDENTIFIER>}
        alternativeSingle(
            { match(TokenType.IDENTIFIER) },
            { match(TokenType.NUMBER) },
            { match(TokenType.STRING) },
            { match(TokenType.CONST_IDENTIFIER) }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }
}