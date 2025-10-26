package pl.weronka.golonka.volatune

data class Configuration(
    val client: ClientConfiguration,
)

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
