package getJson

import getJson.annotations.Mapping
import getJson.annotations.Param
import getJson.annotations.Path

/**
 * Example controller demonstrating the use of the `GetJson` framework.
 */
@Mapping("api")
class Controller {

    /**
     * Returns a list of integers.
     *
     * @return A list of integers.
     */
    @Mapping("ints")
    fun demo(): List<Int> = listOf(1, 2, 3)

    /**
     * Returns a pair of strings as a JSON object.
     *
     * @return A pair of strings.
     */
    @Mapping("pair")
    fun obj(): Pair<String, String> = Pair("um", "dois")

    /**
     * Returns the given path parameter with an exclamation mark appended.
     *
     * @param parameter The path parameter.
     * @return The modified path parameter.
     */
    @Mapping("path/{parameter}")
    fun path(@Path parameter: String): String = "$parameter!"

    /**
     * Returns a map with the given text repeated a specified number of times.
     *
     * @param n The number of repetitions.
     * @param text The text to repeat.
     * @return A map containing the repeated text.
     */
    @Mapping("args")
    fun args(@Param n: Int, @Param text: String): Map<String, String> =
        mapOf(text to text.repeat(n))
}