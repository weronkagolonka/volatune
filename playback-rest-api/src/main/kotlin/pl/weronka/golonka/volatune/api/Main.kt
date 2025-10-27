package pl.weronka.golonka.volatune.api

import io.ktor.client.HttpClient
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.bearer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.di.DI
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.routing.routing
import pl.weronka.golonka.volatune.api.routes.getCurrentPlayback
import pl.weronka.golonka.volatune.api.routes.getCurrentUser
import pl.weronka.golonka.volatune.client.SpotifyClient

// TODO consider running Jetty server
fun main() {
    // port configurable
    embeddedServer(Netty, 8080) {
        installDependencies()
        installPlugins()
        configureRouting()
    }.start()
}

fun Application.installDependencies() {
    val config = Configuration.load() // TODO replace with ktor config
    val httpClient = HttpClient()

    install(DI) {
        dependencies {
            provide<SpotifyClient> { SpotifyClient(httpClient, config.client.spotify) }
        }
    }
}

fun Application.installPlugins() {
    install(Authentication) {
        bearer("service-provider-token") {
            authenticate { it }
        }
    }
    install(ContentNegotiation) {
        json()
    }
}

fun Application.configureRouting() {
    routing {
        getCurrentUser(dependencies)
        getCurrentPlayback(dependencies)
    }
}
