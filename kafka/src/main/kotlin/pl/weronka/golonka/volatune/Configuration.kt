package pl.weronka.golonka.volatune

data class KafkaConfiguration(
    val bootstrapServers: List<String>,
    val schema: SchemaConfiguration,
    val topic: String,
    val consumerGroup: String,
)

data class SchemaConfiguration(
    val url: String,
    val autoRegister: Boolean = false,
    val useLatestVersion: Boolean = true,
)
