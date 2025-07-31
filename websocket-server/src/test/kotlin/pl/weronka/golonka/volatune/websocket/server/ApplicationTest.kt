package pl.weronka.golonka.volatune.websocket.server

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.schema
import com.uber.h3core.H3Core
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import pl.weronka.golonka.volatune.KafkaConfiguration
import pl.weronka.golonka.volatune.SchemaConfiguration
import pl.weronka.golonka.volatune.common.domain.Playback
import pl.weronka.golonka.volatune.common.test.KafkaCleanerListener
import pl.weronka.golonka.volatune.common.test.SchemaRegistererListener
import pl.weronka.golonka.volatune.common.test.TestContainers
import pl.weronka.golonka.volatune.kafka.PlaybackConsumer
import pl.weronka.golonka.volatune.kafka.PlaybackProducer
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ApplicationTest :
    DescribeSpec({
        val kafkaTopic = "volatune-playbacks"
        val schema = Avro.schema<Playback>()
        val h3 = H3Core.newInstance()

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
                socket =
                    SocketConfiguration(
                        pingPeriod = 5.seconds,
                    ),
                proximityInMeters = 300.0,
            )

        val producer = PlaybackProducer(config.kafka, h3)
        val consumer = PlaybackConsumer(config.kafka)

        it("should receive all produced records via websocket") {
            val playbacks =
                listOf(
                    Playback.getTestInstance(),
                    Playback.getTestInstance(),
                    Playback.getTestInstance(),
                )

            // same location as test records
            val userLocation = playbacks.first().location

            playbacks.forEach { producer.send(it) }

            val receivedPlaybacks = mutableListOf<Playback>()

            testApplication {
                application {
                    dependencies.provide<Configuration> { config }
                    dependencies.provide<PlaybackConsumer> { consumer }
                    configureSocket()
                    playbackEndpoint()
                }
                val client =
                    createClient {
                        install(WebSockets)
                        // install(ContentNegotiation) { json() }
                    }

                client.webSocket("/playback", {
                    url {
                        parameters[QueryParams.LATITUDE] = userLocation.latitude.toString()
                        parameters[QueryParams.LONGITUDE] = userLocation.longitude.toString()
                    }
                }) {
                    val collectPlaybacksJob =
                        launch {
                            for (frame in incoming) {
                                val playback = Json.decodeFromString<Playback>((frame as Frame.Text).readText())
                                receivedPlaybacks += playback
                            }
                        }

                    eventually(1.minutes) { receivedPlaybacks.size shouldBe 3 }

                    // Clean closure
                    close(CloseReason(CloseReason.Codes.NORMAL, "timeout reached"))
                    collectPlaybacksJob.join()
                }
            }

            receivedPlaybacks shouldContainOnly playbacks
        }

        // TODO test
        // - missing query params
        // - invalid query params
        // - some records filtered out due to too big distance
    })
