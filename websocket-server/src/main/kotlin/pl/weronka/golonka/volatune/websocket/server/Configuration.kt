package pl.weronka.golonka.volatune.websocket.server

import pl.weronka.golonka.volatune.KafkaConfiguration
import pl.weronka.golonka.volatune.common.config.loadConfiguration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class Configuration(
    val kafka: KafkaConfiguration,
    val socket: SocketConfiguration,
    val proximity: Double,
) {
    companion object {
        fun load(): Configuration =
            loadConfiguration(
                applicationConfFile = "server.conf",
            )
    }
}

data class SocketConfiguration(
    val pingPeriod: Duration = 30.seconds,
)
