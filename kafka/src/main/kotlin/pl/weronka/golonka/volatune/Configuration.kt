package pl.weronka.golonka.volatune

import pl.weronka.golonka.volatune.common.domain.config.loadConfiguration

data class KafkaConfiguration(
    val bootstrapServers: List<String>,
    val schema: SchemaConfiguration,
    val topic: String,
    val consumerGroup: String,
) {
    companion object {
        fun load(): KafkaConfiguration = loadConfiguration()
    }
}

data class SchemaConfiguration(
    val url: String,
    val autoRegister: Boolean = false,
    val useLatestVersion: Boolean = true,
)
