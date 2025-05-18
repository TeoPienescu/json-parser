import model.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

/**
 * Utility object that converts Kotlin objects into a custom JSON model representation.
 *
 * Supported types:
 * - Primitives: Int, Double, Boolean, String
 * - Enums (as strings)
 * - Lists (converted recursively)
 * - Maps with String keys
 * - Kotlin data classes (converted to JsonObject)
 */
object JsonModelConverter {

    /**
     * Converts any Kotlin object into a corresponding [JsonValue] instance.
     *
     * @param obj The object to convert. Can be null, a primitive, enum, collection, map, or data class.
     * @return A [JsonValue] representation of the object.
     * @throws IllegalArgumentException if the object type is not supported.
     */
    fun toJsonValue(obj: Any?): JsonValue {
        return when (obj) {
            null -> JsonNull

            // Primitive types
            is Int -> JsonNumber(obj)
            is Double -> JsonNumber(obj)
            is Boolean -> JsonBoolean(obj)
            is String -> JsonString(obj)

            // List of elements (recursively converted)
            is List<*> -> JsonArray(obj.map { toJsonValue(it) })

            // Map with string keys
            is Map<*, *> -> {
                if (obj.keys.all { it is String }) {
                    JsonObject(LinkedHashMap(
                        obj.mapKeys { it.key as String }
                           .mapValues { toJsonValue(it.value) }
                    ))
                } else {
                    throw IllegalArgumentException("Unsupported type: ${obj::class}")
                }
            }

            // Enum values converted to their name string
            is Enum<*> -> JsonString(obj.name)

            // Data classes converted to JsonObject by reflecting on constructor parameters
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
