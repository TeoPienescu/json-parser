package model

import JsonValidator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JsonValidatorTest {

    @Test
    fun `validateJsonObjectKeys returns true for valid JSON objects`() {
        val validJsonObject = JsonObject(
            mapOf(
                "name" to JsonString("John"),
                "age" to JsonNumber(30),
                "isStudent" to JsonBoolean(false)
            )
        )
        assertTrue(JsonValidator.validateJsonObjectKeys(validJsonObject))
    }

    @Test
    fun `validateJsonObjectKeys returns true for JSON objects with duplicate keys`() {
        val jsonObject = JsonObject(
            mapOf(
                "name" to JsonString("John"),
                "name" to JsonString("Doe")
            )
        )
        //because the keys are not unique, the last one will be taken
        assertTrue(JsonValidator.validateJsonObjectKeys(jsonObject))
    }

    @Test
    fun `validateJsonObjectKeys returns false for JSON objects with blank keys`() {
        val invalidJsonObject = JsonObject(
            mapOf(
                "" to JsonString("Invalid"),
                "age" to JsonNumber(30)
            )
        )
        assertFalse(JsonValidator.validateJsonObjectKeys(invalidJsonObject))
    }

    @Test
    fun `validateJsonArrayTypes returns true for arrays with same type elements`() {
        val validJsonArray = JsonArray(
            listOf(JsonNumber(1), JsonNumber(2), JsonNumber(3))
        )
        assertTrue(JsonValidator.validateJsonArrayTypes(validJsonArray))
    }

    @Test
    fun `validateJsonArrayTypes returns false for arrays with mixed types`() {
        val invalidJsonArray = JsonArray(
            listOf(JsonNumber(1), JsonString("Invalid"), JsonBoolean(true))
        )
        assertFalse(JsonValidator.validateJsonArrayTypes(invalidJsonArray))
    }

    @Test
    fun `validateJsonArrayTypes returns false for arrays containing null values`() {
        val invalidJsonArray = JsonArray(
            listOf(JsonNumber(1), JsonNull, JsonNumber(3))
        )
        assertFalse(JsonValidator.validateJsonArrayTypes(invalidJsonArray))
    }
}