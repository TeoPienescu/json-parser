import model.JsonArray
import model.JsonNull
import model.JsonObject
import model.Traversable


object JsonValidator {
    fun validateJsonObjectKeys(traversableStructure: Traversable): Boolean {
        var isValid = true
        traversableStructure.accept { value ->
            println(value)
            if (value is JsonObject) {
                isValid = hasUniqueNonBlankKeys(value)
                if (!isValid) {
                    return@accept
                }
            }
        }
        return isValid
    }

    fun validateJsonArrayTypes(traversableStructure: Traversable): Boolean {
        var isValid = true
        traversableStructure.accept { value ->
            if (value is JsonArray) {
                val types = value.elements.map { it::class }.toSet()
                if (types.size > 1 || types.contains(JsonNull::class)) {
                    isValid = false
                }
            }
        }
        return isValid
    }

    private fun hasUniqueNonBlankKeys(jsonObject: JsonObject): Boolean {
        val keys = jsonObject.properties.keys
        return keys.all { it.isNotBlank() } && keys.size == keys.toSet().size
    }
}