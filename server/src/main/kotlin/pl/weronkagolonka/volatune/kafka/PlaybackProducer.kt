package pl.weronkagolonka.volatune.kafka

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.encodeToByteArray
import com.github.avrokotlin.avro4k.schema
import io.confluent.kafka.serializers.KafkaAvroSerializer
import kotlinx.serialization.encodeToByteArray
import org.apache.avro.Schema
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import pl.weronkagolonka.volatune.KafkaConfiguration
import pl.weronkagolonka.volatune.domain.events.Playback
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PlaybackProducer(
    config: KafkaConfiguration,
    private val avroInstance: Avro,
    private val topic: String,
    private val schema: Schema,
) {
    private val producer =
        KafkaProducer<String, ByteArray>(
            buildMap {
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to config.bootstrapServers
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.name
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to KafkaAvroSerializer::class.java.name
                "schema.registry.url" to config.schema.url
                "auto.register.schemas" to config.schema.autoRegister
                "use.latest.version" to config.schema.useLatestVersion
            },
        )

    suspend fun send(playback: Playback) {
        val record =
            ProducerRecord(
                topic,
                playback.location,
                avroInstance.encodeToByteArray<Playback>(playback),
            )

        suspendCoroutine<RecordMetadata> { continuation ->
            producer.send(record) { metadata, exception ->
                exception?.let { continuation::resumeWithException }
                    ?: continuation.resume(metadata)
            }
        }
    }

    fun close() = producer.close()
}
