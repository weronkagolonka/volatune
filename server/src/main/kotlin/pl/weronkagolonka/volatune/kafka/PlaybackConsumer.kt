package pl.weronkagolonka.volatune.kafka

import com.github.avrokotlin.avro4k.Avro
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromByteArray
import org.apache.avro.Schema
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import pl.weronkagolonka.volatune.KafkaConfiguration
import pl.weronkagolonka.volatune.domain.events.Playback
import java.time.Duration

class PlaybackConsumer(
    config: KafkaConfiguration,
    private val avroInstance: Avro,
    private val topic: String,
    private val schema: Schema,
) {
    private val consumer =
        KafkaConsumer<String, ByteArray>(
            buildMap {
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to config.bootstrapServers
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to KafkaAvroDeserializer::class.java.name
                "schema.registry.url" to config.schema.url
                "auto.register.schemas" to config.schema.autoRegister
                "use.latest.version" to config.schema.useLatestVersion
                "specific.avro.reader" to true
            },
        )

    fun CoroutineScope.launchPlaybackConsumer(onMessage: (Playback) -> Unit): Job =
        launch(Dispatchers.IO) {
            consumer.subscribe(listOf(topic))
            runCatching {
                while (isActive) {
                    val records = consumer.poll(Duration.ofMillis(500)) // inspect preferred duration
                    records.forEach { record ->
                        avroInstance.decodeFromByteArray<Playback>(record.value())
                    }
                }
            }
        }
}
