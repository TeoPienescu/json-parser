package getJson

import com.sun.net.httpserver.HttpServer
import getJson.annotations.Mapping
import java.net.InetSocketAddress
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

/**
 * A lightweight JSON-based HTTP server framework.
 *
 * @param controllers A list of controller classes annotated with `@Mapping` to define routes.
 */
class GetJson(vararg controllers: KClass<*>) {
    private val routes = mutableMapOf<String, Handler>()
    private var server: HttpServer? = null

    init {
        for (controllerKClass in controllers) {
            val controllerInstance = controllerKClass.constructors.first().call()
            val classMapping = controllerKClass.findAnnotation<Mapping>()?.value ?: ""

            for (function in controllerKClass.declaredMemberFunctions) {
                val funcMapping = function.findAnnotation<Mapping>()?.value ?: continue
                val fullPath = normalize("/$classMapping/$funcMapping")
                routes[fullPath] = Handler(controllerInstance, function)
            }
        }
    }

    /**
     * Starts the HTTP server on the specified port.
     *
     * @param port The port number to bind the server to.
     */
    fun start(port: Int) {
        val server = HttpServer.create(InetSocketAddress(port), 0)
        for ((path, handler) in routes) {
            println(path)
            server.createContext("/") { exchange ->
                val requestPath = exchange.requestURI.path
                val matchedHandler = routes.entries.find { (pattern, _) -> match(pattern, requestPath) }?.value

                if (matchedHandler == null) {
                    exchange.sendResponseHeaders(404, 0)
                    exchange.responseBody.close()
                    return@createContext
                }

                val response = matchedHandler.handle(exchange)
                val bytes = response.toByteArray()
                exchange.responseHeaders.add("Content-Type", "application/json")
                exchange.sendResponseHeaders(200, bytes.size.toLong())
                exchange.responseBody.use { it.write(bytes) }
            }
        }
        server.executor = null
        server.start()
        println("Server started on port $port")
    }

    /**
     * Stops the HTTP server if it is running.
     */
    fun stop() {
        if (server != null) {
            server?.stop(0)
            println("Server stopped")
        } else {
            println("Server is not running")
        }
    }

    /**
     * Matches a request path against a route pattern.
     *
     * @param pattern The route pattern, which may include path parameters.
     * @param actual The actual request path.
     * @return `true` if the pattern matches the actual path, `false` otherwise.
     */
    fun match(pattern: String, actual: String): Boolean {
        val patternParts = pattern.trim('/').split("/")
        val actualParts = actual.trim('/').split("/")

        if (patternParts.size != actualParts.size) return false

        return patternParts.zip(actualParts).all { (p, a) -> p.startsWith("{") || p == a }
    }

    /**
     * Normalizes a path by removing duplicate slashes and trailing slashes.
     *
     * @param path The path to normalize.
     * @return The normalized path.
     */
    private fun normalize(path: String): String = path.replace("//", "/").trimEnd('/')
}