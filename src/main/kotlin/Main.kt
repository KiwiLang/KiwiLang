import java.io.File
import com.buttersus.frontend.lexical.*
import com.buttersus.frontend.syntax.*
import com.kiwilang.frontend.lexical.Lexer
import com.kiwilang.frontend.syntax.Parser

fun main() {
    val source = File("src/main/resources/main.kiwi").readText()

    val lexer = Lexer()
    val parser = Parser(lexer)

//    lexer.load(source)
//    lexer.tokens().forEach {
//        println(it)
//    }

    lexer.load(source)
    parser.load(source)

    println(
        parser.parse().toSimplifiedColoredTreeString()
    )
}