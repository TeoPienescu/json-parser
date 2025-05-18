package getJson

import JsonModelConverter
import com.sun.net.httpserver.HttpExchange
import getJson.annotations.Param
import getJson.annotations.Path
import java.net.URI
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation

/**
 * Represents a request handler for a specific controller function.
 *
 * This class is responsible for:
 * - Parsing HTTP request parameters (from query strings or path segments)
 * - Matching parameters to function annotations
 * - Invoking the controller method with extracted arguments
 * - Converting the return value to JSON using JsonModelConverter
 *
 * @param instance The instance of the controller class
 * @param function The method to invoke when this handler is triggered
 */
class Handler(
    private val instance: Any,
    private val function: KFunction<*>
) {

    /**
     * Handles an HTTP exchange by:
     * - Extracting query parameters and path segments
     * - Mapping them to function parameters based on @Path or @Param annotations
     * - Calling the controller function with appropriate arguments
     * - Serializing the result to a JSON string
     *
     * @param exchange The incoming HTTP request/response context
     * @return A JSON string as the response
     */
    fun handle(exchange: HttpExchange): String {
        val uri = exchange.requestURI
        val pathSegments = uri.path.split("/").filter { it.isNotEmpty() }

        // Extract query parameters into a map
        val queryParams = URI.create(uri.toString()).query
            ?.split("&")
            ?.map { it.split("=") }
            ?.associate { it[0] to it[1] } ?: emptyMap()

        val args = mutableListOf<Any?>()

        // Map function parameters to values using annotations
        for ((index, param) in function.parameters.withIndex()) {
            if (index == 0) continue  // Skip the 'this' instance parameter

            val annPath = param.findAnnotation<Path>()
            val annParam = param.findAnnotation<Param>()

            val value = when {
                annPath != null -> pathSegments.last() // Currently supports only last segment
                annParam != null -> convert(queryParams[param.name!!], param.type.classifier as KClass<*>)
                else -> null
            }

            args.add(value)
        }

        // Invoke the function with the parsed arguments
        val result = function.call(instance, *args.toTypedArray())

        // Convert the result to a JSON string
        return JsonModelConverter.toJsonValue(result).serialize()
    }

    /**
     * Converts a string to a target Kotlin type (supports Int and String).
     *
     * @param value The input string value
     * @param targetType The target class to convert to
     * @return The converted value, or null if unsupported
     */
    private fun convert(value: String?, targetType: KClass<*>): Any? = when (targetType) {
        Int::class -> value?.toInt()
        String::class -> value
        else -> null
    }
}
