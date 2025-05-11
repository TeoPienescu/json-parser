import getJson.GetJson
import getJson.annotations.Mapping
import getJson.annotations.Param
import getJson.annotations.Path

@Mapping("api")
class Controller {
    @Mapping("ints")
    fun demo(): List<Int> = listOf(1, 2, 3)

    @Mapping("pair")
    fun obj(): Pair<String, String> = Pair("um", "dois")

    @Mapping("path/{x}")
    fun path(@Path x: String): String = "$x!"

    @Mapping("args")
    fun args(@Param n: Int, @Param text: String): Map<String, String> =
        mapOf(text to text.repeat(n))
}

fun main() {
    val app = GetJson(Controller::class)
    app.start(8080)
}