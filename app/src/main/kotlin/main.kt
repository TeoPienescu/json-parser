import getJson.Controller
import getJson.GetJson

/**
 * Main function to start the server.
 */
fun main() {
    val app = GetJson(Controller::class)
    app.start(8080)
}