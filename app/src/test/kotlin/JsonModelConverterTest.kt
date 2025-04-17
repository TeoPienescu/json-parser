package model

import kotlin.test.Test
import kotlin.test.assertEquals

data class Course(
    val name: String,
    val credits: Int,
    val evaluation: List<EvalItem>
)
data class EvalItem(
    val name: String,
    val percentage: Double,
    val mandatory: Boolean,
    val type: EvalType?
)
enum class EvalType {
    TEST, PROJECT, EXAM
}


class JsonModelConverterTest {
    private val course = Course(
        "PA", 6, listOf(
            EvalItem("quizzes", .2, false, null),
            EvalItem("project", .8, true,
                EvalType.PROJECT)
        )
    )
    private val jsonString = """{"name":"PA", "credits":6, "evaluation":[{"name":"quizzes", "percentage":0.2, "mandatory":false, "type":null}, {"name":"project", "percentage":0.8, "mandatory":true, "type":"PROJECT"}]}"""

    @Test
    fun `test JsonModelConverter`() {
        val jsonValue = JsonModelConverter.toJsonValue(course)
        println(jsonValue.serialize())

        assertEquals(jsonString, jsonValue.serialize())
    }
}