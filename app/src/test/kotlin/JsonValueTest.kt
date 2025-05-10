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
    fun `test JsonBoolean is equal`() {
        val jsonTrue1 = JsonBoolean(true)
        val jsonTrue2 = JsonBoolean(true)
        val jsonFalse = JsonBoolean(false)
        assertEquals(jsonTrue1, jsonTrue2)
        assertNotEquals(jsonTrue1, jsonFalse)
    }

    @Test
    fun `test JsonNumber serialization`() {
        val jsonNumber = JsonNumber(123)
        assertEquals("123", jsonNumber.serialize())
    }

    @Test
    fun `test JsonNumber is equal`() {
        val jsonNumber1 = JsonNumber(123)
        val jsonNumber2 = JsonNumber(123)
        val jsonNumber3 = JsonNumber(456)
        assertEquals(jsonNumber1, jsonNumber2)
        assertNotEquals(jsonNumber1, jsonNumber3)
    }

    @Test
    fun `test JsonString serialization`() {
        val jsonString = JsonString("Hello, World!")
        assertEquals("\"Hello, World!\"", jsonString.serialize())
    }

    @Test
    fun `test JsonString is equal`() {
        val jsonString1 = JsonString("Hello")
        val jsonString2 = JsonString("Hello")
        val jsonString3 = JsonString("World")
        assertEquals(jsonString1, jsonString2)
        assertNotEquals(jsonString1, jsonString3)
    }

    @Test
    fun `test JsonArray serialization`() {
        val jsonArray = JsonArray(
            listOf(JsonString("A"), JsonNumber(1), JsonBoolean(true), JsonNull)
        )
        assertEquals("[\"A\", 1, true, null]", jsonArray.serialize())
    }

    @Test
    fun `test JsonArray is equal`() {
        val jsonArray1 = JsonArray(
            listOf(JsonString("A"), JsonNumber(1), JsonBoolean(true), JsonNull)
        )
        val jsonArray2 = JsonArray(
            listOf(JsonString("A"), JsonNumber(1), JsonBoolean(true), JsonNull)
        )
        val jsonArray3 = JsonArray(
            listOf(JsonString("B"), JsonNumber(2), JsonBoolean(false), JsonNull)
        )
        assertEquals(jsonArray1, jsonArray2)
        assertNotEquals(jsonArray1, jsonArray3)
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
    fun `test JsonObject is equal`() {
        val jsonObject1 = JsonObject(
            mapOf(
                "name" to JsonString("John"),
                "age" to JsonNumber(30)
            )
        )
        val jsonObject2 = JsonObject(
            mapOf(
                "name" to JsonString("John"),
                "age" to JsonNumber(30)
            )
        )
        val jsonObject3 = JsonObject(
            mapOf(
                "name" to JsonString("Doe"),
                "age" to JsonNumber(25)
            )
        )
        assertEquals(jsonObject1, jsonObject2)
        assertNotEquals(jsonObject1, jsonObject3)
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