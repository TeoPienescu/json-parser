package getJson

import JsonModelConverter
import com.sun.net.httpserver.HttpExchange
import getJson.annotations.Param
import getJson.annotations.Path
import java.net.URI
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation

class Handler(
    private val instance: Any,
    private val function: KFunction<*>
) {
    fun handle(exchange: HttpExchange): String {
        val uri = exchange.requestURI
        val pathSegments = uri.path.split("/").filter { it.isNotEmpty() }

        val queryParams = URI.create(uri.toString()).query
            ?.split("&")?.map { it.split("=") }
            ?.associate { it[0] to it[1] } ?: emptyMap()


        val args = mutableListOf<Any?>()
        for ((index, param) in function.parameters.withIndex()) {
            if (index == 0) continue
            val annPath = param.findAnnotation<Path>()
            val annParam = param.findAnnotation<Param>()

            val value = when {
                annPath != null -> pathSegments.last()
                annParam != null -> convert(queryParams[param.name!!], param.type.classifier as KClass<*>)
                else -> null
            }

            args.add(value)
        }

        val result = function.call(instance, *args.toTypedArray())
        return JsonModelConverter.toJsonValue(result)
            .serialize()
    }

    private fun convert(value: String?, targetType: KClass<*>): Any? = when (targetType) {
        Int::class -> value?.toInt()
        String::class -> value
        else -> null
    }
}