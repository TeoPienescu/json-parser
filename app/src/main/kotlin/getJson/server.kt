package getJson

import com.sun.net.httpserver.HttpServer
import getJson.annotations.Mapping
import java.net.InetSocketAddress
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

class GetJson(vararg controllers: KClass<*>) {
    private val routes = mutableMapOf<String, Handler>()

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
    fun start(port: Int){
        val server = HttpServer.create(InetSocketAddress(port), 0)
        for ((path, handler) in routes) {
            println("Registering path: $path")

            server.createContext("/") { exchange ->
                val requestPath = exchange.requestURI.path
                val handler = routes.entries.find { (pattern, _) -> match(pattern, requestPath) }?.value

                if (handler == null) {
                    exchange.sendResponseHeaders(404, 0)
                    return@createContext
                }

                val response = handler.handle(exchange)
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

    fun match(pattern: String, actual: String): Boolean {
        val patternParts = pattern.trim('/').split("/")
        val actualParts = actual.trim('/').split("/")

        if (patternParts.size != actualParts.size) return false

        return patternParts.zip(actualParts).all { (p, a) -> p.startsWith("{") || p == a }
    }
    private fun normalize(path: String): String = path.replace("//", "/").trimEnd('/')
}