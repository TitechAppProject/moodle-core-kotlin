package app.titech.moodle.request

import app.titech.moodle.request.`interface`.RestAPIRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SiteInfoRequest(wsToken: String) : RestAPIRequest<SiteInfoResponse> {
    override val httpMethod: String = "GET"
    override val queryParameters: Map<String, Any>? = mapOf(
        "moodlewsrestformat" to "json",
        "wstoken" to wsToken,
        "wsfunction" to "core_webservice_get_site_info"
    )

    override fun decode(string: String): SiteInfoResponse {
        val decoder = Json {
            ignoreUnknownKeys = true
        }
        return decoder.decodeFromString(string)
    }
}

@Serializable
data class SiteInfoResponse(
    val userid: Int,
    val username: String,
    val fullname: String,
    val firstname: String,
    val lastname: String
)