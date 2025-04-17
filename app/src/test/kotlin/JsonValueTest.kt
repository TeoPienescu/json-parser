package model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JsonValueTest {

    @Test
    fun `test JsonNull serialization`() {
        val jsonNull = JsonNull
        assertEquals("null", jsonNull.serialize())
    }

    @Test
    fun `test JsonBoolean serialization`() {
        val jsonTrue = JsonBoolean(true)
        val jsonFalse = JsonBoolean(false)
        assertEquals("true", jsonTrue.serialize())
        assertEquals("false", jsonFalse.serialize())
    }

    @Test
    fun `test JsonNumber serialization`() {
        val jsonNumber = JsonNumber(123)
        assertEquals("123", jsonNumber.serialize())
    }

    @Test
    fun `test JsonString serialization`() {
        val jsonString = JsonString("Hello, World!")
        assertEquals("\"Hello, World!\"", jsonString.serialize())
    }

    @Test
    fun `test JsonArray serialization`() {
        val jsonArray = JsonArray(
            listOf(JsonString("A"), JsonNumber(1), JsonBoolean(true), JsonNull)
        )
        assertEquals("[\"A\", 1, true, null]", jsonArray.serialize())
    }

    @Test
    fun `test JsonArray filter`() {
        val jsonArray = JsonArray(
            listOf(JsonNumber(1), JsonNumber(2), JsonNumber(3))
        )

        val filteredArray = jsonArray.filter { jsonValue ->
            (jsonValue as JsonNumber).value.toInt() > 1
        }
        assertEquals("[2, 3]", filteredArray.serialize())
    }

    @Test
    fun `test JsonArray map`() {
        val jsonArray = JsonArray(
            listOf(JsonNumber(1), JsonNumber(2), JsonNumber(3))
        )

        val mappedArray = jsonArray.map { jsonValue ->
            JsonNumber((jsonValue as JsonNumber).value.toInt() * 2)
        }
        assertEquals("[2, 4, 6]", mappedArray.serialize())
    }

    @Test
    fun `test JsonObject serialization`() {
        val jsonObject = JsonObject(
            mapOf(
                "name" to JsonString("John"),
                "age" to JsonNumber(30),
                "isStudent" to JsonBoolean(false)
            )
        )
        assertEquals("{\"name\":\"John\", \"age\":30, \"isStudent\":false}", jsonObject.serialize())
    }

    @Test
    fun `test JsonObject filter`() {
        val jsonObject = JsonObject(
            mapOf(
                "name" to JsonString("John"),
                "age" to JsonNumber(30),
                "isStudent" to JsonBoolean(false)
            )
        )
        val filteredObject = jsonObject.filter { it.key != "age" }
        assertEquals("{\"name\":\"John\", \"isStudent\":false}", filteredObject.serialize())
    }
}