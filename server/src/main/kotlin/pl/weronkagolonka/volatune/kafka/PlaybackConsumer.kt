package pl.weronkagolonka.volatune.kafka

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import pl.weronkagolonka.volatune.KafkaConfiguration
import pl.weronkagolonka.volatune.domain.events.Playback
import pl.weronkagolonka.volatune.domain.events.PlaybackKafkaSerializer
import java.time.Duration

class PlaybackConsumer(
    config: KafkaConfiguration,
    private val topic: String,
) {
    private val consumer =
        KafkaConsumer<String, Playback>(
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to config.bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to config.consumerGroup,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.canonicalName,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to PlaybackKafkaSerializer::class.java.canonicalName,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                "schema.registry.url" to config.schema.url,
                "auto.register.schemas" to config.schema.autoRegister,
                "use.latest.version" to config.schema.useLatestVersion,
                "specific.avro.reader" to true,
            ),
        )

    // TODO consumer or API filters out the nearest events.
    //  When scaling, this should be handled by stream processing tools
    //  + derived topics based on regions
    fun getPlaybackEvents(): Flow<Playback> =
        callbackFlow {
            /**
             * webSocket("/playback-stream") {
             *     playbackConsumer.playbackEventsFlow().collect { playback ->
             *         sendSerialized(playback)  // uses kotlinx.serialization
             *     }
             * }
             *
             */

            consumer.subscribe(listOf(topic))
            try {
                while (isActive) {
                    consumer.poll(Duration.ofMillis(5000)).forEach {
                        trySend(it.value())
                    }
                    yield()
                }
            } catch (e: Throwable) {
                close(e)
            } finally {
                consumer.close()
                close()
            }
        }
}
