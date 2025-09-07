package pl.weronka.golonka.volatune.kafka

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.schema
import com.uber.h3core.H3Core
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainOnly
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import pl.weronka.golonka.volatune.KafkaConfiguration
import pl.weronka.golonka.volatune.SchemaConfiguration
import pl.weronka.golonka.volatune.common.domain.Playback
import pl.weronka.golonka.volatune.common.test.SchemaRegistererListener
import pl.weronka.golonka.volatune.common.test.TestContainers

class PlaybackProducerConsumerTest :
    DescribeSpec({
        val kafkaTopic = "volatune-playbacks"
        val schema = Avro.schema<Playback>()
        val h3 = H3Core.newInstance()

        listeners(
            SchemaRegistererListener(
                "http://${TestContainers.schemaRegistry.host}:${TestContainers.schemaRegistry.firstMappedPort}",
                kafkaTopic,
                schema,
            ),
        )

        val config =
            KafkaConfiguration(
                bootstrapServers = listOf(TestContainers.kafka.bootstrapServers),
                schema =
                    SchemaConfiguration(
                        url = "http://${TestContainers.schemaRegistry.host}:${TestContainers.schemaRegistry.firstMappedPort}",
                    ),
                topic = kafkaTopic,
                consumerGroup = "test",
            )

        val producer = PlaybackProducer(config, h3)
        val consumer = PlaybackConsumer(config)

        val playback = Playback.getTestInstance()

        afterSpec {
            producer.close()
            consumer.stopPollingPlaybacks()
        }

        it("should correctly send and consume a record") {
            val consumedRecords = mutableListOf<Playback>()
            consumer.starPollingPlaybacks(this)

            val job =
                launch {
                    consumer
                        .playbackEvents
                        .take(1)
                        .collect { consumedRecords += it }
                    println("finished")
                }

            producer.send(playback)
            job.join()

            consumer.stopPollingPlaybacks()

            consumedRecords shouldContainOnly listOf(playback)
        }
    })
