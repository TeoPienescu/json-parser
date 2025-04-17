import kotlin.reflect.KClass

interface JsonConverter {
    fun convert(kClass: KClass<*>): String
}