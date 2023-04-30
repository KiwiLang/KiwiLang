import com.buttersus.frontend.lexical.*
import com.buttersus.frontend.syntax.*
import java.io.File

val lexer = Lexer()
val parser = Parser()

fun main() {
    val source = File("src/main/resources/main.kiwi").readText()
    try {
        lexer.load(source)
        parser.link(lexer)
        parser.load(source)
        val result = parser.parse()
        println(
            result.toSimplifiedColoredTreeString()
        )
    } catch (e: SyntaxError) {
        println("\u001B[31m${e.message}\u001B[0m")
    } catch (e: LexicalError) {
        println("\u001B[31m${e.message}\u001B[0m")
    }
}