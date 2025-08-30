package app.titech.moodle

import app.titech.moodle.request.`interface`.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*

interface APIClient {
    suspend fun <Response> send(request: Request<Response>): Response
}

class APIClientImpl(
    val baseURL: URL,
    val userAgent: String
) : APIClient {
    override suspend fun <Response> send(request: Request<Response>): Response =
        withContext(Dispatchers.IO) {
            val baseURI = baseURL.toURI()
            val resolvedURI = baseURI.resolve(baseURI.path + request.path)
            
            val finalURI = request.queryParameters?.let { params ->
                val query = params.entries.joinToString("&") { (key, value) ->
                    "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value.toString(), "UTF-8")}"
                }
                URI(
                    resolvedURI.scheme,
                    resolvedURI.authority,
                    resolvedURI.path,
                    query,
                    resolvedURI.fragment
                )
            } ?: resolvedURI

            val url = finalURI.toURL()
            var connection =
                generateUrlConnection(
                    url,
                    request.httpMethod,
                    request.headerFields,
                    userAgent
                )

            do {
                connection.connect()

                var needRedirect = false
                if (connection.responseCode in 300..399) {
                    val location = connection.headerFields["Location"]!!.first()
                    try {
                        val locationURL = when {
                            location.startsWith("?") -> URL(url.protocol + "://" + url.host + url.path + location)
                            location.startsWith("/") -> URL(url.protocol + "://" + url.host + location)
                            else -> URL(location)
                        }

                        connection =
                            generateUrlConnection(
                                locationURL,
                                "GET",
                                request.headerFields,
                                userAgent
                            )
                        needRedirect = true
                    } catch (e: Exception) {
                        needRedirect = false
                    }
                }
            } while (needRedirect)

            val br = BufferedReader(InputStreamReader(connection.inputStream))

            val sb = StringBuilder()

            for (line in br.readLines()) {
                sb.append(line)
            }

            br.close()

            val html = sb.toString()

            return@withContext request.decode(html)
        }

    private fun generateUrlConnection(
        url: URL,
        httpMethod: String,
        headerFields: Map<String, String>?,
        userAgent: String
    ): HttpURLConnection {
        val connection = url.openConnection() as HttpURLConnection

        headerFields?.forEach {
            connection.setRequestProperty(it.key, it.value)
        }
        connection.setRequestProperty("User-Agent", userAgent)
        connection.requestMethod = httpMethod
        connection.instanceFollowRedirects = false

        return connection
    }
}

class APIClientMock : APIClient {
    private val responseProvider: (Request<*>) -> Any

    constructor(responseProvider: (Request<*>) -> Any) {
        this.responseProvider = responseProvider
    }

    constructor(jsonResponse: String) {
        this.responseProvider = { request ->
            request.decode(jsonResponse) as Any
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Response> send(request: Request<Response>): Response {
        return responseProvider(request) as Response
    }
}