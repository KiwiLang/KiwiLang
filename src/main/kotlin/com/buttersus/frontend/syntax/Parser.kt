package com.buttersus.frontend.syntax

import com.buttersus.frontend.lexical.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.reflect.KFunction0

// TODO: Распределить логику класса по разным файлам путём наследования
// TODO: Соблюдать структуру Kotlin грамматики
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
    private fun semis(value: () -> TokenAST?, separator: () -> TokenAST?,
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
        // start: .package? .separator:import* statement*
        // <NEWLINE>? !<EOF> -> "unexpected context"
        // => Module(package, imports, statements)
        val `package` = `package`() ?: TokenAST.Void
        val imports = semis(
            { import() },
            { semis() }
        ) ?: TokenAST.List()
        val statements = list { statement() } ?: TokenAST.List()
        match(TokenType.NEWLINE)
        match(TokenType.EOF) ?: raise("unexpected", "EOF")
        return TokenAST.Module(`package`, imports, statements)
    }

    private fun `package`(): TokenAST? {
        val mark = mark()

        // 'package' .!<PACKAGE_NAME>[enable] -> "wrong package name"
        // !separator -> "expected separator" / "';' or newline"
        // => Package(identifier)
        fun case0(): TokenAST? {
            match("package") ?: return null
            lexer.enable(TokenType.PACKAGE_NAME)
            val identifier = match(TokenType.PACKAGE_NAME) ?: raise("wrong package name", "PACKAGE_NAME")
            semis() ?: raise("expected separator", "';' or newline")
            return TokenAST.Package(identifier)
        }

        case0()?.also { return it }
        reset(mark)

        return null
    }

    private fun import(): TokenAST? {
        val mark = mark()

        // 'import' .!<PACKAGE_NAME>[enable] -> "Expected package name"
        // !separator -> "Expected ';' or newline"
        // => Import(packageName)
        fun case0(): TokenAST? {
            match("import") ?: return null
            lexer.enable(TokenType.IMPORT_PACKAGE_NAME)
            val packageName = match(TokenType.IMPORT_PACKAGE_NAME) ?: raise("wrong package name", "IMPORT_PACKAGE_NAME")
            semis() ?: raise("expected separator", "';' or newline")
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

        // =.expressionStatement !separator -> "expected ';' or newline" / "<NEWLINE> or ';'"
        fun case2(): TokenAST? {
            val first = expressionStatement() ?: return null
            semis() ?: raise("expected separator", "<NEWLINE> or ';'")
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

        // ={ifStatement | whileStatement | forStatement |
        // functionDeclaration | classDeclaration}
        alternativeSingle(
            { ifStatement() },
            { whileStatement() },
            { forStatement() },
            { functionDeclaration() },
            { classDeclaration() }
        )?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun declaration(): TokenAST? = memoize(::declaration) {
        val mark = mark()

        // ={variableDeclaration | functionDeclaration | classDeclaration}
        alternativeSingle(
            { variableDeclaration() },
            { functionDeclaration() },
            { classDeclaration() }
        )?.also { return@memoize it }

        return@memoize null
    }

    private fun ifStatement(): TokenAST? = memoize(::ifStatement) {
        val mark = mark()

        // 'if' '(' .!expression -> "wrong condition" !')' -> "missing closing parenthesis"
        // !'{' -> "missing opening brace" .statement* !'}' -> "missing closing brace"
        // {'else' { .statement | '{' .statement* !'}' -> "missing closing brace"}}?
        // => IfStatement(condition, body, elseBody)
        fun case0(): TokenAST? {
            match("if") ?: return null
            match("(") ?: return null
            val condition = expression() ?: raise("wrong condition", "expression")
            match(")") ?: raise("missing closing parenthesis", "')'")
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

        // 'if' .!expression -> "wrong condition" '->'?
        // !'{' -> "missing opening brace" .statement* !'}' -> "missing closing brace"
        // {'else' { .statement | '{' .statement* !'}' -> "missing closing brace"}}?
        // => IfStatement(condition, body, elseBody)
        fun case1(): TokenAST? {
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

        case1()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun whileStatement(): TokenAST? = memoize(::whileStatement) {
        val mark = mark()

        // 'while' '(' .!expression -> "wrong condition" !')' -> "missing closing parenthesis"
        // !'{' -> "missing opening brace" .statement* !'}' -> "missing closing brace"
        // => WhileStatement(condition, body)
        fun case0(): TokenAST? {
            match("while") ?: return null
            match("(") ?: return null
            val condition = expression() ?: raise("wrong condition", "expression")
            match(")") ?: raise("missing closing parenthesis", "')'")
            match("{") ?: raise("missing opening brace", "'{'")
            val body = list { statement() } ?: TokenAST.List()
            match("}") ?: raise("missing closing brace", "'}'")
            return TokenAST.WhileStatement(condition, body)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // 'while' .!expression -> "wrong condition" '->'?
        // !'{' -> "missing opening brace" .statement* !'}' -> "missing closing brace"
        // => WhileStatement(condition, body)
        fun case1(): TokenAST? {
            match("while") ?: return null
            val condition = expression() ?: raise("wrong condition", "expression")
            match("->")
            match("{") ?: raise("missing opening brace", "'{'")
            val body = list { statement() } ?: TokenAST.List()
            match("}") ?: raise("missing closing brace", "'}'")
            return TokenAST.WhileStatement(condition, body)
        }

        case1()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun forStatement(): TokenAST? = memoize(::forStatement) {
        val mark = mark()

        // 'for' '(' .<IDENTIFIER> 'in' .!expression -> "wrong iterable" !')' -> "missing closing parenthesis"
        // !'{' -> "missing opening brace" .statement* !'}' -> "missing closing brace"
        // => ForInStatement(variable, iterable, body)
        fun case0(): TokenAST? {
            match("for") ?: return null
            match("(") ?: return null
            val variable = match(TokenType.IDENTIFIER) ?: raise("wrong variable name", "IDENTIFIER")
            match("in") ?: return null
            val iterable = expression() ?: raise("wrong iterable", "expression")
            match(")") ?: raise("missing closing parenthesis", "')'")
            match("{") ?: raise("missing opening brace", "'{'")
            val body = list { statement() } ?: TokenAST.List()
            match("}") ?: raise("missing closing brace", "'}'")
            return TokenAST.ForInStatement(variable, iterable, body)
        }
        
        case0()?.also { return@memoize it }
        reset(mark)

        // 'for' .<IDENTIFIER> 'in' .!expression -> "wrong iterable" '->'?
        // !'{' -> "missing opening brace" .statement* !'}' -> "missing closing brace"
        // => ForInStatement(variable, iterable, body)
        fun case1(): TokenAST? {
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

        case1()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun functionDeclaration(): TokenAST? = memoize(::functionDeclaration) {
        val mark = mark()

        // 'fun' .!<IDENTIFIER> -> "wrong function name"
        // {'(' .{2}parameters?[=TokenAST.List(TokenAST.List(), TokenAST.List())] !')' -> "missing closing parenthesis"}?
        // {'->' .!type -> "wrong return type"}?
        // {'<-' .!',':expression+ -> "missing event"}?
        // !{'{' .statement* !'}' -> "missing closing brace"
        // | '=' .!expression -> "wrong expression"} -> "wrong function body" / "statement or expression"
        // => FunctionDeclaration(name, parameters, assignedParameters, returns, events, body)
        fun case0(): TokenAST? {
            match("fun") ?: return null
            val name = match(TokenType.IDENTIFIER) ?: raise("wrong function name", "IDENTIFIER")
            var second: TokenAST.List = TokenAST.List(TokenAST.List(), TokenAST.List())
            optional(
                { match("(") },
                { parameters()?.also { second = it } ?: TokenAST.Void },
                { match(")") ?: raise("missing closing parenthesis", "')'") }
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
            var events: TokenAST.List = TokenAST.List()
            optional(
                { match("<-") },
                { semis(
                    { expression() },
                    { match(",") }
                )?.also { events = it } ?: raise("missing event", "expression") }
            ) ?: run {
                events = TokenAST.List()
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
            return TokenAST.FunctionDeclaration(name, second[0], second[1], returns, events, body)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }
    
    // CLASSES
    // =======

    private fun classDeclaration(): TokenAST? = memoize(::classDeclaration) {
        val mark = mark()

        // 'class' .!<IDENTIFIER> -> "wrong class name"
        // {'(' .{2}parameters?[=TokenAST.List(TokenAST.List(), TokenAST.List())]
        // !')' -> "missing closing parenthesis"}?
        // {':' .!','.expression+ -> "missing superclass"}?
        // TODO: Доделать тело класса
        // => ClassDeclaration(name, parameters, assignedParameters, superclasses)
        fun case0(): TokenAST? {
            match("class") ?: return null
            val name = match(TokenType.IDENTIFIER) ?: raise("wrong class name", "IDENTIFIER")
            var second: TokenAST.List = TokenAST.List(TokenAST.List(), TokenAST.List())
            optional(
                { match("(") },
                { parameters()?.also { second = it } ?: TokenAST.Void },
                { match(")") ?: raise("missing closing parenthesis", "')'") }
            ) ?: run {
                second = TokenAST.List(TokenAST.List(), TokenAST.List())
            }
            var superclasses: TokenAST.List = TokenAST.List()
            optional(
                { match(":") },
                { semis(
                    { expression() },
                    { match(",") }
                )?.also { superclasses = it } ?: raise("missing superclass", "expression") }
            ) ?: run {
                superclasses = TokenAST.List()
            }
            return TokenAST.ClassDeclaration(name, second[0], second[1], superclasses)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun parameters(): TokenAST.List? = memoize(::parameters) {
        val mark = mark()

        // .',':{=castParameter ?!'='}+
        // {',' .',':assignedParameter+}? ','? !?=')' -> "missing closing parenthesis"
        fun case0(): TokenAST? {
            val first = semis(
                {
                    val first = castParameter()
                    lookahead { match("=") }?.also { return@semis null }
                    first
                },
                { match(",") }
            ) ?: return null
            var second: TokenAST.List = TokenAST.List()
            optional(
                { match(",") },
                {
                    semis(
                        { assignedParameter() },
                        { match(",") }
                    )?.also { second = it }
                }
            ) ?: run {
                second = TokenAST.List()
            }
            match(",") ?: TokenAST.Void
            lookahead { match(")") } ?: raise("missing closing parenthesis", "')'")
            return TokenAST.List(first, second)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // =.={TokenAST.List()}.',':assignedParameter+ ','? !?=')' -> "missing closing parenthesis"
        fun case1(): TokenAST? {
            val first = TokenAST.List()
            val second = semis(
                { assignedParameter() },
                { match(",") }
            ) ?: return null
            match(",") ?: TokenAST.Void
            lookahead { match(")") } ?: raise("missing closing parenthesis", "')'")
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

    private fun castParameter(): TokenAST? = memoize(::castParameter) {
        val mark = mark()

        // .<IDENTIFIER> ':' .!type -> "wrong data type"
        // 'as' .expression -> "wrong cast expression"
        // => CastParameter(name, type, value)
        fun case0(): TokenAST? {
            val name = match(TokenType.IDENTIFIER) ?: return null
            match(":") ?: return null
            val type = type() ?: raise("wrong data type", "type")
            match("as") ?: return null
            val value = expression() ?: raise("wrong cast expression", "expression")
            return TokenAST.CastParameter(name, type, value)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // =parameter
        parameter()?.also { return@memoize it }
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

    private fun expression(): TokenAST? = memoize(::expression) {
        val mark = mark()

        // .disjunction '?' .!expression -> "wrong first expression""
        // !':' -> "missing ':'" .!expression -> "wrong second expression"
        // => TernaryExpression(condition, trueExpression, falseExpression)
        fun case0(): TokenAST? {
            val condition = disjunction() ?: return null
            match("?") ?: return null
            val trueExpression = expression() ?: raise("wrong first expression", "expression")
            match(":") ?: raise("missing ':'", "':'")
            val falseExpression = expression() ?: raise("wrong second expression", "expression")
            return TokenAST.TernaryExpression(condition, trueExpression, falseExpression)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // '[' .!',':expression* ','? !']' -> "missing closing parenthesis"
        // => ListExpression(items)
        fun case1(): TokenAST? {
            match("[") ?: return null
            val items = semis(
                { expression() },
                { match(",") }
            ) ?: TokenAST.List()
            match(",")
            match("]") ?: raise("missing closing parenthesis", "']'")
            return TokenAST.ListExpression(items)
        }

        case1()?.also { return@memoize it }
        reset(mark)

        // '{' .',':{=expression ':' =!expression -> "wrong value"}+ ','? !'}' -> "missing closing brace"
        // => MapExpression(keys={
        // TokenAST.List(*first.map{(it as TokenAST.List)[0]}.toTypedArray())
        // }, values={
        // TokenAST.List(*first.map{(it as TokenAST.List)[1]}.toTypedArray())
        // })
        fun case2(): TokenAST? {
            match("{") ?: return null
            val first = semis(
                {
                    val first = expression() ?: return@semis null
                    match(":") ?: return@semis null
                    val second = expression() ?: raise("wrong value", "expression")
                    return@semis TokenAST.List(first, second)
                },
                { match(",") }
            ) ?: return null
            match(",")
            match("}") ?: raise("missing closing brace", "'}'")
            return TokenAST.MapExpression(
                TokenAST.List(
                    *first.map {
                        (it as TokenAST.List)[0]
                    }.toTypedArray()
                ),
                TokenAST.List(
                    *first.map {
                        (it as TokenAST.List)[1]
                    }.toTypedArray()
                )
            )
        }

        case2()?.also { return@memoize it }
        reset(mark)

        // '{' .!',':expression* ','? !'}' -> "missing closing brace"
        // => SetExpression(items)
        fun case3(): TokenAST? {
            match("{") ?: return null
            val items = semis(
                { expression() },
                { match(",") }
            ) ?: TokenAST.List()
            match(",")
            match("}") ?: raise("missing closing brace", "'}'")
            return TokenAST.SetExpression(items)
        }

        case3()?.also { return@memoize it }
        reset(mark)

        // '(' .{',':expression{2} | (expression) ?=','} ','?
        // !')' -> "missing closing parenthesis"
        // => TupleExpression(items)
        fun case4(): TokenAST? {
            match("(") ?: return null
            var items = TokenAST.List()
            alternativeMulti(
                {
                    semis(
                        { expression() },
                        { match(",") },
                        2
                    )?.also { items = it } ?: return@alternativeMulti false
                    true
                },
                {
                    expression()?.also {
                        items = TokenAST.List(it)
                    } ?: return@alternativeMulti false
                    lookahead{ match(",") } ?: return@alternativeMulti false
                    true
                }
            ) ?: run {
                reset(mark)
                return@case4 null
            }
            match(",")
            match(")") ?: raise("missing closing parenthesis", "')'")
            return TokenAST.TupleExpression(items)
        }

        case4()?.also { return@memoize it }
        reset(mark)

        // =disjunction
        disjunction()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun disjunction(): TokenAST? = memoizeLR(::disjunction) {
        val mark = mark()

        // .disjunction '||' .!conjunction -> "wrong expression" => Or(left, right)
        fun case0(): TokenAST? {
            val left = disjunction() ?: return null
            match("||") ?: return null
            val right = conjunction() ?: raise("wrong expression", "conjunction")
            return TokenAST.Or(left, right)
        }

        case0()?.also { return@memoizeLR it }
        reset(mark)

        // =conjunction
        conjunction()?.also { return@memoizeLR it }
        reset(mark)

        return@memoizeLR null
    }

    private fun conjunction(): TokenAST? = memoizeLR(::conjunction) {
        val mark = mark()

        // .conjunction '&&' .!inversion -> "wrong expression" => And(left, right)
        fun case0(): TokenAST? {
            val left = conjunction() ?: return null
            match("&&") ?: return null
            val right = inversion() ?: raise("wrong expression", "inversion")
            return TokenAST.And(left, right)
        }

        case0()?.also { return@memoizeLR it }
        reset(mark)

        // =inversion
        inversion()?.also { return@memoizeLR it }
        reset(mark)

        return@memoizeLR null
    }

    private fun inversion(): TokenAST? = memoize(::inversion) {
        val mark = mark()

        // '!' .!comparison -> "wrong expression" => Not(value)
        fun case0(): TokenAST? {
            match("!") ?: return null
            val value = comparison() ?: raise("wrong expression", "inversion")
            return TokenAST.Not(value)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // =comparison
        comparison()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }

    private fun comparison(): TokenAST? = memoizeLR(::comparison) {
        val mark = mark()

        // .comparison '==' .!range -> "wrong expression" => Equal(left, right)
        fun case0(): TokenAST? {
            val left = comparison() ?: return null
            match("==") ?: return null
            val right = range() ?: raise("wrong expression", "range")
            return TokenAST.Equal(left, right)
        }

        case0()?.also { return@memoizeLR it }
        reset(mark)

        // .comparison '!=' .!range -> "wrong expression" => NotEqual(left, right)
        fun case1(): TokenAST? {
            val left = comparison() ?: return null
            match("!=") ?: return null
            val right = range() ?: raise("wrong expression", "range")
            return TokenAST.NotEqual(left, right)
        }

        case1()?.also { return@memoizeLR it }
        reset(mark)

        // .comparison '<' .!range -> "wrong expression" => LessThan(left, right)
        fun case2(): TokenAST? {
            val left = comparison() ?: return null
            match("<") ?: return null
            val right = range() ?: raise("wrong expression", "range")
            return TokenAST.LessThan(left, right)
        }

        case2()?.also { return@memoizeLR it }
        reset(mark)

        // .comparison '<=' .!range -> "wrong expression" => LessThanOrEqual(left, right)
        fun case3(): TokenAST? {
            val left = comparison() ?: return null
            match("<=") ?: return null
            val right = range() ?: raise("wrong expression", "range")
            return TokenAST.LessThanOrEqual(left, right)
        }

        case3()?.also { return@memoizeLR it }
        reset(mark)

        // .comparison '>' .!range -> "wrong expression" => GreaterThan(left, right)
        fun case4(): TokenAST? {
            val left = comparison() ?: return null
            match(">") ?: return null
            val right = range() ?: raise("wrong expression", "range")
            return TokenAST.GreaterThan(left, right)
        }

        case4()?.also { return@memoizeLR it }
        reset(mark)

        // .comparison '>=' .!range -> "wrong expression" => GreaterThanOrEqual(left, right)
        fun case5(): TokenAST? {
            val left = comparison() ?: return null
            match(">=") ?: return null
            val right = range() ?: raise("wrong expression", "range")
            return TokenAST.GreaterThanOrEqual(left, right)
        }

        case5()?.also { return@memoizeLR it }
        reset(mark)

        // =range
        range()?.also { return@memoizeLR it }
        reset(mark)

        return@memoizeLR null
    }

    private fun range(): TokenAST? = memoizeLR(::range) {
        val mark = mark()

        // .range '..' .!sum -> "wrong expression" => Range(left, right)
        fun case0(): TokenAST? {
            val left = range() ?: return null
            match("..") ?: return null
            val right = sum() ?: raise("wrong expression", "sum")
            return TokenAST.Range(left, right)
        }

        case0()?.also { return@memoizeLR it }
        reset(mark)

        // =sum
        sum()?.also { return@memoizeLR it }
        reset(mark)

        return@memoizeLR null
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

        // .primary '(' .{2}arguments?[=TokenAST.List(TokenAST.List(), TokenAST.List())]
        // !')' -> "missing closing parenthesis"
        // => Call(function, generics, arguments, namedArguments)
        fun case1(): TokenAST? {
            val function = primary() ?: return null
            match("(") ?: return null
            val third = arguments() ?: TokenAST.List(TokenAST.List(), TokenAST.List())
            match(")") ?: raise("missing closing parenthesis", "')'")
            return TokenAST.Call(function, third[0], third[1])
        }

        case1()?.also { return@memoizeLR it }
        reset(mark)

//        // .primary '<' .',':expression* ','? !'>' -> "missing closing angle parenthesis"
//        // => Generic(parent, generics)
//        fun case2(): TokenAST? {
//            val parent = primary() ?: return null
//            match("<") ?: return null
//            val generics = separator(
//                { expression() },
//                { match(",") }
//            ) ?: raise("missing closing angle parenthesis", "'>'")
//            match("?")
//            match(">") ?: raise("missing closing angle parenthesis", "'>'")
//            return TokenAST.Generic(parent, generics)
//        }
//
//        case2()?.also { return@memoizeLR it }
//        reset(mark)

        // .primary '[' .':':{!expression -> "wrong expression"}* !']' -> "missing closing square parenthesis"
        // => Index(parent, indices)
        fun case3(): TokenAST? {
            val parent = primary() ?: return null
            match("[") ?: return null
            val indices = semis(
                { expression() ?: raise("wrong expression", "expression") },
                { match(":") }
            ) ?: raise("missing closing square parenthesis", "']'")
            match("]") ?: raise("missing closing square parenthesis", "']'")
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
        // {',' .',':assignedArgument+}? ','? !?=')' -> "missing closing parenthesis"
        fun case0(): TokenAST? {
            val first = semis(
                {
                    val first = expression()
                    lookahead { match("=") }?.also { return@semis null }
                    first
                },
                { match(",") }
            ) ?: return null
            var second: TokenAST.List = TokenAST.List()
            optional(
                { match(",") },
                {
                    semis(
                        { assignedArgument() },
                        { match(",") }
                    )?.also { second = it }
                }
            ) ?: run {
                second = TokenAST.List()
            }
            match(",") ?: TokenAST.Void
            lookahead { match(")") } ?: raise("missing closing parenthesis", "')'")
            return TokenAST.List(first, second)
        }

        case0()?.also { return@memoize it }
        reset(mark)

        // =.={TokenAST.List()}.',':assignedArgument+ ','? !?=')' -> "missing closing parenthesis"
        fun case1(): TokenAST? {
            val first = TokenAST.List()
            val second = semis(
                { assignedArgument() },
                { match(",") }
            ) ?: return null
            match(",") ?: TokenAST.Void
            lookahead { match(")") } ?: raise("missing closing parenthesis", "')'")
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

        // ='(' .!expression -> "wrong expression" ?!',' !')' -> "missing closing parenthesis"
        fun case0(): TokenAST? {
            match("(") ?: return null
            val expression = expression() ?: raise("wrong expression", "expression")
            lookahead { match(",") }?.also { return null }
            match(")") ?: raise("missing closing parenthesis", "')'")
            return expression
        }

        case0()?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }
    
    // GENERAL TEMPLATES
    // =================
    
    fun semis(): TokenAST? = memoize(::semis) {
        val mark = mark()

        // =<NEWLINE> | ';'
        match(TokenType.NEWLINE)?.also { return@memoize it }
        reset(mark)

        return@memoize null
    }
}