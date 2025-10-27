package pl.weronka.golonka.volatune.api.routes

import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.di.DependencyRegistry
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import pl.weronka.golonka.volatune.Configs
import pl.weronka.golonka.volatune.Paths
import pl.weronka.golonka.volatune.client.SpotifyClient
import pl.weronka.golonka.volatune.common.domain.dto.StreamingProvider

fun Route.getCurrentPlayback(dependencies: DependencyRegistry) {
    authenticate(Configs.BEARER_AUTH) {
        get("/{${Paths.PROVIDER}}/me") {
            val spotifyClient: SpotifyClient by dependencies
            val token = bearerToken()

            val playback =
                when (streamingProvider()) {
                    StreamingProvider.SPOTIFY -> spotifyClient.getCurrentPlayback(token)
                }
            call.respond(playback)
        }
    }
}
