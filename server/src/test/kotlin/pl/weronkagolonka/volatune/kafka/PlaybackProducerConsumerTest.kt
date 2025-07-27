package pl.weronkagolonka.volatune.kafka

import KafkaCleanerListener
import SchemaRegistererListener
import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.schema
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainOnly
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import pl.weronkagolonka.volatune.Configuration
import pl.weronkagolonka.volatune.KafkaConfiguration
import pl.weronkagolonka.volatune.SchemaConfiguration
import pl.weronkagolonka.volatune.domain.events.Playback
import pl.weronkagolonka.volatune.getTestInstance
import kotlin.time.Duration.Companion.seconds

class PlaybackProducerConsumerTest :
    DescribeSpec({
        val kafkaTopic = "volatune-playbacks"
        val schema = Avro.schema<Playback>()

        listeners(
            KafkaCleanerListener(TestContainers.kafka.bootstrapServers),
            SchemaRegistererListener(
                "http://${TestContainers.schemaRegistry.host}:${TestContainers.schemaRegistry.firstMappedPort}",
                kafkaTopic,
                schema,
            ),
        )

        val config =
            Configuration(
                kafka =
                    KafkaConfiguration(
                        bootstrapServers = listOf(TestContainers.kafka.bootstrapServers),
                        schema =
                            SchemaConfiguration(
                                url = "http://${TestContainers.schemaRegistry.host}:${TestContainers.schemaRegistry.firstMappedPort}",
                            ),
                        topic = kafkaTopic,
                        consumerGroup = "test",
                    ),
            )
        val avro = Avro

        val producer = PlaybackProducer(config.kafka, avro)
        val consumer = PlaybackConsumer(config.kafka, kafkaTopic)

        val playback = Playback.getTestInstance()

        afterSpec {
            producer.close()
        }

        it("should correctly send nd consume a record") {
            val consumedRecords = mutableListOf<Playback>()
            val job =
                launch {
                    consumer
                        .getPlaybackEvents()
                        .take(1)
                        .collect { consumedRecords += it }
                }

            producer.send(playback)

            eventually(5.seconds) {
                consumedRecords shouldContainOnly listOf(playback)
            }

            job.cancel()
        }
    })
