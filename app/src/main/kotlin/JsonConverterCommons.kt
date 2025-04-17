import model.*
import kotlin.reflect.KClass

// map kotlin basic class types to json value types
internal val basicConverter: Map<KClass<*>, (Any) -> JsonValue> = mapOf(
    String::class to { JsonString(it as String) },
    Int::class to { JsonNumber(it as Int) },
    Double::class to { JsonNumber(it as Double) },
    Boolean::class to { JsonBoolean(it as Boolean) },
)