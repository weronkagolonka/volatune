package pl.weronka.golonka.volatune.websocket.server

import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.di.DI
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import kotlinx.serialization.json.Json
import pl.weronka.golonka.volatune.common.domain.Location
import pl.weronka.golonka.volatune.common.domain.isWithinProximity
import pl.weronka.golonka.volatune.kafka.PlaybackConsumer

// TODO setup KTOR config with port number, etc.
// TODO hoplite file needs different naming
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    configureDependencies()
    configureSocket()
    playbackEndpoint()
}

fun Application.configureDependencies() {
    val config = Configuration.load()

    install(DI) {
        dependencies {
            provide<Configuration> { config }

            provide<PlaybackConsumer> {
                PlaybackConsumer(
                    config = config.kafka,
                )
            }
        }
    }
}

fun Application.configureSocket() {
    val config: Configuration by dependencies

    install(WebSockets) {
        pingPeriod = config.socket.pingPeriod
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
}

fun Application.playbackEndpoint() {
    routing {
        webSocket("/playback") {
            val consumer: PlaybackConsumer by dependencies
            val config: Configuration by dependencies

            val latitude = latitudeQueryParam() ?: return@webSocket
            val longitude = longitudeQueryParam() ?: return@webSocket
            val userLocation = Location(latitude, longitude)

            // TODO test
            // - missing query params
            // - invalid query params
            // - some records filtered out due to too big distance
            consumer.getPlaybackEvents().collect { playback ->
                (userLocation to playback.location).let { distance ->
                    if (distance.isWithinProximity(config.proximity)) {
                        sendSerialized(playback)
                    }
                }
            }
        }
    }
}
