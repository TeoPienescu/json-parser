package getJson.annotations

/**
 * Annotation to mark a parameter as a query parameter in a GET request.
 *
 * @property value The name of the query parameter.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path