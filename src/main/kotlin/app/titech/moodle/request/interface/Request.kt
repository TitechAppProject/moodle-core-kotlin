package app.titech.moodle.request.`interface`

interface Request<Response> {
    val httpMethod: String
    val path: String
    val queryParameters: Map<String, Any>?
    val headerFields: Map<String, String>?
    fun decode(string: String): Response
}

interface RestAPIRequest<Response> : Request<Response> {
    override val path: String
        get() = "/webservice/rest/server.php"

    override val headerFields: Map<String, String>?
        get() = mapOf(
            "Content-Type" to "application/json"
        )
}