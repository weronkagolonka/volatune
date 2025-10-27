package pl.weronka.golonka.volatune.client

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import pl.weronka.golonka.volatune.api.StreamingServiceConfiguration
import pl.weronka.golonka.volatune.common.domain.PlaybackItem
import pl.weronka.golonka.volatune.common.domain.User
import pl.weronka.golonka.volatune.dto.SpotifyUser
import pl.weronka.golonka.volatune.dto.StreamingServicePlaybackItem
import pl.weronka.golonka.volatune.exceptions.GetCurrentPlaybackException
import pl.weronka.golonka.volatune.exceptions.GetCurrentUserException

sealed class StreamingServiceClient(
    client: HttpClient,
    config: StreamingServiceConfiguration,
) {
    abstract suspend fun getUserProfile(token: String): User

    abstract suspend fun getCurrentPlayback(token: String): PlaybackItem

    fun JsonObject.extractPrimitiveValue(path: String): JsonPrimitive =
        this[path]?.jsonPrimitive ?: throw IllegalStateException("Response body does not contain path $path")
}

/**
 * Connecting to the client (Spotify):
 * User must log in to Spotify to obtain their ID
 * Use PKCE Flow to authorize the user - https://developer.spotify.com/documentation/web-api/tutorials/code-pkce-flow <- done from the UI
 * So endpoint requires Bearer token, and the service method will verify it on Spotify's side
 *
 * The client will be used in the REST API service
 * Need proper mapping of client errors to error responses
 */

class SpotifyClient(
    val client: HttpClient,
    val config: StreamingServiceConfiguration,
) : StreamingServiceClient(client, config) {
    override suspend fun getUserProfile(token: String): User =
        runCatching {
            val url = "${config.baseApiUrl}/me"
            val response =
                client
                    .get(url) {
                        headers {
                            append("Authorization", "Bearer $token")
                        }
                    }
            if (response.status != HttpStatusCode.OK) {
                throw GetCurrentUserException(this::javaClass.name, statusCode = response.status.value)
            }

            return Json.decodeFromString<SpotifyUser>(response.bodyAsText()).toUser()
        }.getOrElse {
            throw GetCurrentUserException(this::javaClass.name, cause = it)
        }

    override suspend fun getCurrentPlayback(token: String): PlaybackItem =
        runCatching {
            val url = "${config.baseApiUrl}/me/player"
            val response =
                client
                    .get(url) {
                        headers {
                            append("Authorization", "Bearer $token")
                        }
                    }
            if (response.status != HttpStatusCode.OK) {
                throw GetCurrentPlaybackException(this::javaClass.name, statusCode = response.status.value)
            }

            return Json.decodeFromString<StreamingServicePlaybackItem>(response.bodyAsText()).toPlaybackItem()
        }.getOrElse {
            throw GetCurrentPlaybackException(this::javaClass.name, cause = it)
        }
}
