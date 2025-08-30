package app.titech.moodle.request

import app.titech.moodle.request.`interface`.RestAPIRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class UserEnrolCourseRequest(userId: Int, wsToken: String) : RestAPIRequest<UserEnrolCoursesResponse> {
    override val httpMethod: String = "GET"
    override val queryParameters: Map<String, Any>? = mapOf(
        "moodlewsrestformat" to "json",
        "wstoken" to wsToken,
        "wsfunction" to "core_enrol_get_users_courses",
        "userid" to userId
    )

    override fun decode(string: String): UserEnrolCoursesResponse {
        val decoder = Json {
            ignoreUnknownKeys = true
        }
        return decoder.decodeFromString(string)
    }
}


typealias UserEnrolCoursesResponse = List<UserEnrolCourseResponse>

@Serializable
data class UserEnrolCourseResponse(
    val id: Int,
    val shortname: String,
    val fullname: String,
    val category: Int,
    val startdate: Int,
    val enddate: Int
)