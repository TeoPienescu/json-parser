import model.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

object JsonModelConverter {
    fun toJsonValue(obj: Any?): JsonValue {
        return when (obj) {
            null -> JsonNull
            is Int -> JsonNumber(obj)
            is Double -> JsonNumber(obj)
            is Boolean -> JsonBoolean(obj)
            is String -> JsonString(obj)
            is List<*> -> JsonArray(obj.map { toJsonValue(it) })
            is Map<*, *> -> {
                if (obj.keys.all { it is String }) {
                    JsonObject(LinkedHashMap(obj.mapKeys { it.key as String }.mapValues { toJsonValue(it.value) }))
                } else {
                    throw IllegalArgumentException("Unsupported type: ${obj::class}")

                }
            }
            is Enum<*> -> JsonString(obj.name)
            else -> {
                val kClass: KClass<*> = obj::class
                if (kClass.isData) {
                    val properties = LinkedHashMap<String, JsonValue>()
                    val constructorParams = kClass.primaryConstructor?.parameters ?: emptyList()
                    constructorParams.forEach { param ->
                        val prop = kClass.memberProperties.find { it.name == param.name }
                        if (prop != null) {
                            prop.isAccessible = true
                            properties[prop.name] = toJsonValue(prop.getter.call(obj))
                        }
                    }
                    JsonObject(properties)
                } else {
                    throw IllegalArgumentException("Unsupported type: ${obj::class}")
                }
            }
        }
    }
}