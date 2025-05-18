import model.JsonArray
import model.JsonNull
import model.JsonObject
import model.Traversable

/**
 * Utility object for validating JSON structures represented by custom model classes.
 */
object JsonValidator {

    /**
     * Validates that all [JsonObject]s within a traversable structure:
     * - Have non-blank keys
     * - Have unique keys (no duplicates)
     *
     * @param traversableStructure The root structure to validate (e.g., JsonObject or JsonArray)
     * @return `true` if all objects are valid, `false` otherwise
     */
    fun validateJsonObjectKeys(traversableStructure: Traversable): Boolean {
        var isValid = true

        traversableStructure.accept { value ->
            if (value is JsonObject) {
                isValid = hasUniqueNonBlankKeys(value)
                // Stop further traversal if invalid
                if (!isValid) return@accept
            }
        }

        return isValid
    }

    /**
     * Validates that all [JsonArray]s within a traversable structure:
     * - Contain elements of the same type
     * - Do not include JsonNull
     *
     * @param traversableStructure The root structure to validate
     * @return `true` if all arrays are valid, `false` otherwise
     */
    fun validateJsonArrayTypes(traversableStructure: Traversable): Boolean {
        var isValid = true

        traversableStructure.accept { value ->
            if (value is JsonArray) {
                val types = value.elements.map { it::class }.toSet()
                // Invalid if types are mixed or include nulls
                if (types.size > 1 || types.contains(JsonNull::class)) {
                    isValid = false
                }
            }
        }

        return isValid
    }

    /**
     * Helper method to check if a JsonObject has:
     * - No blank keys
     * - All keys are unique
     *
     * @param jsonObject The object to inspect
     * @return `true` if all keys are non-blank and unique, `false` otherwise
     */
    private fun hasUniqueNonBlankKeys(jsonObject: JsonObject): Boolean {
        val keys = jsonObject.properties.keys
        return keys.all { it.isNotBlank() } && keys.size == keys.toSet().size
    }
}
