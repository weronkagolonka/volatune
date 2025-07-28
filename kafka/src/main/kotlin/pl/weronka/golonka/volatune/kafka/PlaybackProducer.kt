package pl.weronka.golonka.volatune.kafka

import com.github.avrokotlin.avro4k.Avro
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import pl.weronka.golonka.volatune.KafkaConfiguration
import pl.weronka.golonka.volatune.common.domain.Playback
import pl.weronka.golonka.volatune.common.domain.PlaybackKafkaSerializer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PlaybackProducer(
    private val config: KafkaConfiguration,
    private val avroInstance: Avro,
) {
    private val producer =
        KafkaProducer<String, Playback>(
            mapOf(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to config.bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.canonicalName,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to PlaybackKafkaSerializer::class.java.canonicalName,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
                "schema.registry.url" to config.schema.url,
                "auto.register.schemas" to config.schema.autoRegister,
                "use.latest.version" to config.schema.useLatestVersion,
            ),
        )

    suspend fun send(playback: Playback) {
        val record =
            ProducerRecord(
                config.topic,
                playback.location,
                playback,
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
