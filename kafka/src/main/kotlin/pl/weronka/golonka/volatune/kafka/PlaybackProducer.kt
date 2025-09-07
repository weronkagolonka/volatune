package pl.weronka.golonka.volatune.kafka

import com.uber.h3core.H3Core
import kotlinx.coroutines.suspendCancellableCoroutine
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

class PlaybackProducer(
    private val config: KafkaConfiguration,
    private val h3: H3Core,
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

    suspend fun send(playback: Playback): RecordMetadata =
        suspendCancellableCoroutine { continuation ->
            val record =
                ProducerRecord(
                    config.topic,
                    playback.location.h3Index(h3).toString(),
                    playback,
                )

            val future =
                producer.send(record) { metadata, exception ->
                    if (exception != null) {
                        continuation.resumeWithException(exception)
                    } else {
                        continuation.resume(metadata)
                    }
                }

            continuation.invokeOnCancellation {
                future.cancel(true)
            }
        }

    fun close() = producer.close()
}
