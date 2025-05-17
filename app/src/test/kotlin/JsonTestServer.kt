package model

import Controller
import getJson.GetJson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetJsonIntegrationTest {

    companion object {
        private val client = OkHttpClient()
        private lateinit var server: GetJson

        @BeforeAll
        @JvmStatic
        fun setup() {
            server = GetJson(Controller::class)
            server.start(8080)
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            server.stop()
        }

        private fun get(url: String): String {
            val request = Request.Builder()
                .url("http://localhost:8080$url")
                .build()

            client.newCall(request).execute().use { response ->
                assertTrue(response.isSuccessful)
                return response.body?.string() ?: ""
            }
        }
    }

    @Test
    fun `GET api ints should return list of ints`() {
        val json = get("/api/ints")
        assertEquals("[1, 2, 3]", json)
    }

    @Test
    fun `GET api pair should return pair as JSON object`() {
        val json = get("/api/pair")
        // Order of keys in JSON object is not guaranteed, parse and compare
        assertTrue(json.contains("\"first\":\"um\""))
        assertTrue(json.contains("\"second\":\"dois\""))
    }

    @Test
    fun `GET api path a should return a!`() {
        val json = get("/api/path/a")
        assertEquals("\"a!\"", json)
    }

    @Test
    fun `GET api path b should return b!`() {
        val json = get("/api/path/b")
        assertEquals("\"b!\"", json)
    }

    @Test
    fun `GET api args with n=3 and text=PA should return map with repeated text`() {
        val json = get("/api/args?n=3&text=PA")
        assertEquals("{\"PA\":\"PAPAPA\"}", json)
    }
}
