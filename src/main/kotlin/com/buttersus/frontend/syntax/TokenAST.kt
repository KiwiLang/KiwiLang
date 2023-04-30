package com.buttersus.frontend.syntax

import com.buttersus.frontend.lexical.*
import com.buttersus.frontend.*

sealed class TokenAST {
    companion object Colors {
        // Reset color and background
        /** Used to reset the color and background */
        private const val RESET = "\u001B[0m" + "\u001B[49m"
        // Light Blue
        /** Used for node names */
        private const val NODE_NAME = "\u001B[34m"
        // Black and Red background
        /** Used for missing values */
        private const val NONE = "\u001B[30m" + "\u001B[41m"
        // Yellow
        /** Used for attribute names */
        private const val ATTRIBUTE = "\u001B[33m"
        // Light Green
        /** Used for raw token values */
        private const val TOKEN = "\u001B[32m"
        // Light Purple and Black background
        /** Used for token type */
        private const val LITERAL = "\u001B[35m" + "\u001B[40m"
        // White
        /** Used for braces */
        private const val OP = "\u001B[97m"
        // Black and Gray background
        /** Used for boolean values */
        private const val BOOLEAN = "\u001B[30m" + "\u001B[47m"
    }

    /**
     * Returns the properties of the node.
     */
    open fun properties(): Array<Pair<String, TokenAST>> = emptyArray()

    /**
     * Returns a string representation of the syntax tree.
     */
    open fun toTreeString(): String {
        val props = properties()
        if (props.isEmpty()) {
            return "<${javaClass.simpleName}>"
        }
        return """
<${javaClass.simpleName}> {
${
            props.joinToString("\n") { (name, value) ->
                "$name: ${value.toTreeString()}"
            }.tab()
        }
}
        """.trimIndent()
    }

    /**
     * Returns a colored string representation of the syntax tree.
     */
    open fun toColoredTreeString(): String {
        val props = properties()
        if (props.isEmpty()) {
            return "$NODE_NAME<${javaClass.simpleName}>$RESET"
        }
        return """
$NODE_NAME<${javaClass.simpleName}>$RESET $OP{$RESET
${
            props.joinToString("\n") { (name, value) ->
                "$ATTRIBUTE$name$RESET$OP:$RESET ${value.toColoredTreeString()}"
            }.tab()
        }
$OP}$RESET
        """.trimIndent()
    }

    /**
     * Returns a simplified string representation of the syntax tree.
     */
    open fun toSimplifiedColoredTreeString(): String {
        val props = properties().mapNotNull { (name, value) ->
            if (value.isHavingContent())
                "$ATTRIBUTE$name$RESET$OP:$RESET ${value.toSimplifiedColoredTreeString()}"
            else null
        }
        if (props.isEmpty()) {
            return "$NODE_NAME<${javaClass.simpleName}>$RESET"
        }
        return """
$NODE_NAME<${javaClass.simpleName}>$RESET $OP{$RESET
${props.joinToString("\n").tab()}
$OP}$RESET
        """.trimIndent()
    }

    internal open fun isHavingContent(): Boolean {
        return properties().any { (_, value) -> value.isHavingContent() }
    }

    object Void : TokenAST() {
        override fun toTreeString(): String {
            return "MISSING!"
        }

        override fun toColoredTreeString(): String {
            return "$NONE MISSING! $RESET"
        }

        override fun toSimplifiedColoredTreeString() = toColoredTreeString()

        override fun isHavingContent() = false
    }

    class List(
        vararg items: TokenAST
    ) : TokenAST(), MutableList<TokenAST> by arrayListOf(*items) {
        override fun toTreeString(): String {
            if (isEmpty()) {
                return "L[]"
            }
            return """
L[
${joinToString(",\n") { it.toTreeString() }.tab(1)}
]
            """.trimIndent()
        }

        override fun toColoredTreeString(): String {
            if (isEmpty()) {
                return "${NODE_NAME}L$RESET$OP[]$RESET"
            }
            return """
${NODE_NAME}L$RESET$OP[$RESET
${joinToString(",\n") { it.toColoredTreeString() }.tab(1)}
$OP]$RESET
            """.trimIndent()
        }

        override fun toSimplifiedColoredTreeString(): String {
            val props = mapNotNull {
                if (it.isHavingContent())
                    it.toSimplifiedColoredTreeString()
                else null
            }
            return """
${NODE_NAME}L$RESET$OP[$RESET
${props.joinToString(",\n").tab(1)}
$OP]$RESET
            """.trimIndent()
        }

        override fun isHavingContent() = isNotEmpty()
    }

    data class TokenWrapper(
        val token: TokenInfo
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "token" to this
        )

        override fun toTreeString(): String {
            return "`${token.lexeme}` - ${token.type}"
        }

        override fun toColoredTreeString(): String {
            return "${TOKEN}`${token.lexeme}`$RESET $OP-$RESET $LITERAL ${token.type} $RESET"
        }

        override fun toSimplifiedColoredTreeString(): String {
            return "${TOKEN}`${token.lexeme}`$RESET"
        }

        override fun isHavingContent() = true
    }

    data class Module(
        val `package`: TokenAST,
        val imports: TokenAST,
        val statements: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "package" to `package`,
            "imports" to imports,
            "statements" to statements
        )
    }

    data class Package(
        val name: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "name" to name
        )
    }

    data class Import(
        val name: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "name" to name
        )
    }

    data class IfStatement(
        val condition: TokenAST,
        val body: TokenAST,
        val elseBody: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "condition" to condition,
            "body" to body,
            "elseBody" to elseBody
        )
    }

    data class WhileStatement(
        val condition: TokenAST,
        val body: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "condition" to condition,
            "body" to body
        )
    }

    data class ForInStatement(
        val variable: TokenAST,
        val iterable: TokenAST,
        val body: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "variable" to variable,
            "iterable" to iterable,
            "body" to body
        )
    }

    data class FunctionDeclaration(
        val name: TokenAST,
        val parameters: TokenAST,
        val assignedParameters: TokenAST,
        val returns: TokenAST,
        val events: TokenAST,
        val body: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "name" to name,
            "parameters" to parameters,
            "assignedParameters" to assignedParameters,
            "returns" to returns,
            "events" to events,
            "body" to body
        )
    }

    data class ClassDeclaration(
        val name: TokenAST,
        val parameters: TokenAST,
        val assignedParameters: TokenAST,
        val superclasses: TokenAST,
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "name" to name,
            "parameters" to parameters,
            "assignedParameters" to assignedParameters,
            "superclasses" to superclasses
        )
    }

    data class Parameter(
        val name: TokenAST,
        val type: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "name" to name,
            "type" to type
        )
    }

    data class CastParameter(
        val name: TokenAST,
        val type: TokenAST,
        val value: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "name" to name,
            "type" to type,
            "value" to value
        )
    }

    data class AssignedParameter(
        val name: TokenAST,
        val type: TokenAST,
        val value: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "name" to name,
            "type" to type,
            "value" to value
        )
    }

    data class VariableDeclaration(
        val name: TokenAST,
        val type: TokenAST,
        val value: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "name" to name,
            "type" to type,
            "value" to value
        )
    }

    data class ReturnStatement(
        val value: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "value" to value
        )
    }

    object BreakStatement : TokenAST()
    object ContinueStatement : TokenAST()
    object PassStatement : TokenAST()

    data class AssignedArgument(
        val name: TokenAST,
        val value: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "name" to name,
            "value" to value
        )
    }

    data class Attribute(
        val parent: TokenAST,
        val name: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "parent" to parent,
            "name" to name
        )
    }

    data class Call(
        val function: TokenAST,
        val arguments: TokenAST,
        val namedArguments: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "function" to function,
            "arguments" to arguments,
            "namedArguments" to namedArguments
        )
    }

    data class Generic(
        val parent: TokenAST,
        val generics: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "parent" to parent,
            "generics" to generics
        )
    }

    data class Index(
        val parent: TokenAST,
        val indices: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "parent" to parent,
            "indices" to indices
        )
    }

    data class Add(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class Sub(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class Mul(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class Div(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class Mod(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class Pos(
        val value: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "value" to value
        )
    }

    data class Neg(
        val value: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "value" to value
        )
    }

    sealed class Bool : TokenAST() {
        object True : Bool() {
            override fun toTreeString(): String {
                return "true"
            }

            override fun toColoredTreeString(): String {
                return "$BOOLEAN true $RESET"
            }
        }

        object False : Bool() {
            override fun toTreeString(): String {
                return "false"
            }

            override fun toColoredTreeString(): String {
                return "$BOOLEAN false $RESET"
            }

            override fun toSimplifiedColoredTreeString() = toColoredTreeString()
        }
    }

    object AutoType : TokenAST() {
        override fun toTreeString(): String {
            return "auto"
        }

        override fun toColoredTreeString(): String {
            return "$BOOLEAN auto $RESET"
        }

        override fun toSimplifiedColoredTreeString() = toColoredTreeString()
    }

    object CompileTimeAutoType : TokenAST() {
        override fun toTreeString(): String {
            return "\$auto"
        }

        override fun toColoredTreeString(): String {
            return "$BOOLEAN \$auto $RESET"
        }

        override fun toSimplifiedColoredTreeString() = toColoredTreeString()
    }

    data class ListExpression(
        val items: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "items" to items
        )
    }

    data class SetExpression(
        val items: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "items" to items
        )
    }

    data class MapExpression(
        val keys : TokenAST,
        val values : TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "keys" to keys,
            "values" to values
        )
    }

    data class TupleExpression(
        val items: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "items" to items
        )
    }

    data class Equal(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class NotEqual(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class GreaterThan(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class GreaterThanOrEqual(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class LessThan(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class LessThanOrEqual(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }

    data class Or(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf("left" to left, "right" to right)
    }

    data class And(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf("left" to left, "right" to right)
    }

    data class Not(
        val value: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf("value" to value)
    }

    data class TernaryExpression(
        val condition: TokenAST,
        val trueExpression: TokenAST,
        val falseExpression: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "condition" to condition,
            "trueExpression" to trueExpression,
            "falseExpression" to falseExpression
        )
    }

    data class Range(
        val left: TokenAST,
        val right: TokenAST
    ) : TokenAST() {
        override fun properties(): Array<Pair<String, TokenAST>> = arrayOf(
            "left" to left,
            "right" to right
        )
    }
}
