package pl.weronka.golonka.volatune.kafka

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import pl.weronka.golonka.volatune.KafkaConfiguration
import pl.weronka.golonka.volatune.common.domain.Playback
import pl.weronka.golonka.volatune.common.domain.PlaybackKafkaSerializer
import java.time.Duration

class PlaybackConsumer(
    val config: KafkaConfiguration,
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

    // Replay 0 - users see only those events that were collected after they connected to the socket.
    private val _playbackEvents = MutableSharedFlow<Playback>(replay = 0)
    val playbackEvents = _playbackEvents.asSharedFlow()

    // TODO consumer or API filters out the nearest events.
    //  When scaling, this should be handled by stream processing tools
    //  + derived topics based on regions

    // TODO: polling duration is configurable
    fun starPollingPlaybacks(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            consumer.subscribe(listOf(config.topic))
            try {
                while (isActive) {
                    consumer.poll(Duration.ofMillis(5000)).forEach {
                        _playbackEvents.emit(it.value())
                    }
                }
            } catch (e: Exception) {
                if (e is WakeupException) {
                    println("Consumer shut down")
                } else {
                    e.printStackTrace()
                }
            } finally {
                consumer.close()
            }
        }
    }

    fun stopPollingPlaybacks() {
        consumer.wakeup()
    }
}
