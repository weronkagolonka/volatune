package pl.weronka.golonka.volatune.api

import pl.weronka.golonka.volatune.common.config.loadConfiguration

data class Configuration(
    val serverPort: Int,
    val client: ClientConfiguration,
) {
    companion object {
        fun load() = loadConfiguration<Configuration>()
    }
}

data class ClientConfiguration(
    val spotify: StreamingServiceConfiguration,
    // potentially other streaming services
)

data class StreamingServiceConfiguration(
    val baseApiUrl: String,
    val clientId: String,
    val clientSecret: String,
    val getTokenUrl: String,
)
