package model

sealed class JsonValue {
    abstract fun serialize(): String
}

interface Traversable {
    fun accept(visitor: (JsonValue) -> Unit)
}

data object JsonNull : JsonValue() {
    override fun serialize(): String = "null"
}

data class JsonBoolean(val value: Boolean) : JsonValue() {
    override fun serialize(): String = value.toString()
}

data class JsonNumber(val value: Number) : JsonValue() {
    override fun serialize(): String = value.toString()
}

data class JsonString(val value: String) : JsonValue() {
    override fun serialize(): String = "\"$value\""
}

data class JsonArray(val elements: List<JsonValue>) : JsonValue(), Traversable {
    override fun serialize(): String = elements.joinToString(prefix = "[", postfix = "]") { it.serialize() }

    fun filter(predicate: (JsonValue) -> Boolean): JsonArray {
        return JsonArray(elements.filter(predicate))
    }

    fun map(transform: (JsonValue) -> JsonValue): JsonArray {
        return JsonArray(elements.map(transform))
    }

    override fun accept(visitor: (JsonValue) -> Unit) {
        visitor(this)
        elements.forEach { element ->
            visitor(element)
            if (element is Traversable) {
                element.accept(visitor)
            }
        }
    }
}

data class JsonObject(val properties: Map<String, JsonValue>) : JsonValue(), Traversable {
    override fun serialize(): String = properties.entries.joinToString(prefix = "{", postfix = "}") {
        "\"${it.key}\":${it.value.serialize()}"
    }

    fun filter(predicate: (Map.Entry<String, JsonValue>) -> Boolean): JsonObject {
        return JsonObject(properties.filter(predicate))
    }

    override fun accept(visitor: (JsonValue) -> Unit) {
        visitor(this)
        properties.values.forEach { value ->
            visitor(value)
            if (value is Traversable) {
                value.accept(visitor)
            }
        }
    }
}