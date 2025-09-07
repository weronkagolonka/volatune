package pl.weronka.golonka.volatune.websocket.server

import com.uber.h3core.H3Core
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.testing.testApplication
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import pl.weronka.golonka.volatune.KafkaConfiguration
import pl.weronka.golonka.volatune.SchemaConfiguration
import pl.weronka.golonka.volatune.common.domain.Location
import pl.weronka.golonka.volatune.common.domain.Playback
import pl.weronka.golonka.volatune.kafka.PlaybackConsumer
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ApplicationTest :
    DescribeSpec({
        val kafkaTopic = "volatune-playbacks"
        val h3 = H3Core.newInstance()
        val config =
            Configuration(
                kafka =
                    KafkaConfiguration(
                        bootstrapServers = listOf("test-url"),
                        schema =
                            SchemaConfiguration(
                                url = "http://test-host:test",
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

        val mockConsumer = mockk<PlaybackConsumer>()
        val mockPlaybackFlow = MutableSharedFlow<Playback>()

        fun connectToPlaybackServer(
            request: HttpRequestBuilder.() -> Unit = {},
            action: suspend DefaultClientWebSocketSession.() -> Unit,
        ) {
            testApplication {
                application {
                    dependencies.provide<Configuration> { config }
                    dependencies.provide<PlaybackConsumer> { mockConsumer }
                    configureSocket()
                    pollPlayback()
                }
                val client =
                    createClient {
                        install(WebSockets)
                    }

                client.webSocket("/playback", request, action)
            }
        }

        val playbacks =
            listOf(
                Playback.getTestInstance(),
                Playback.getTestInstance(),
                Playback.getTestInstance(),
            )
        val userLocation = playbacks.first().location
        val receivedPlaybacks = mutableListOf<Playback>()

        beforeTest {
            justRun { mockConsumer.startPollingPlaybacks(any()) }
            every { mockConsumer.playbackEvents } returns mockPlaybackFlow.asSharedFlow()
        }

        afterTest {
            clearAllMocks()
            receivedPlaybacks.removeAll(receivedPlaybacks)
        }

        it("should receive all produced records via websocket") {
            connectToPlaybackServer(request = {
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
                playbacks.forEach { mockPlaybackFlow.emit(it) }

                eventually(1.minutes) { receivedPlaybacks.size shouldBe 3 }

                close(CloseReason(CloseReason.Codes.NORMAL, "timeout reached"))
                collectPlaybacksJob.join()
            }

            receivedPlaybacks shouldContainOnly playbacks
        }

        it("should abort the connection if query params are missing") {
            connectToPlaybackServer {
                val collectPlaybacksJob =
                    launch {
                        for (frame in incoming) {
                            val playback = Json.decodeFromString<Playback>((frame as Frame.Text).readText())
                            receivedPlaybacks += playback
                        }
                    }

                val closeReason = closeReason.await()
                closeReason.shouldNotBeNull()
                closeReason.code shouldBe CloseReason.Codes.CANNOT_ACCEPT.code
                closeReason.message shouldBe "lat query parameter is required"

                collectPlaybacksJob.join()
            }
        }

        it("should abort the connection if query params are invalid") {
            connectToPlaybackServer(request = {
                url {
                    parameters[QueryParams.LATITUDE] = "invalid-value"
                    parameters[QueryParams.LONGITUDE] = "invalid-value"
                }
            }) {
                val collectPlaybacksJob =
                    launch {
                        for (frame in incoming) {
                            val playback = Json.decodeFromString<Playback>((frame as Frame.Text).readText())
                            receivedPlaybacks += playback
                        }
                    }

                val expectedErrorMessage =
                    runCatching {
                        "invalid-value".toDouble()
                    }.getOrElse { it.message }

                val closeReason = closeReason.await()
                closeReason.shouldNotBeNull()
                closeReason.code shouldBe CloseReason.Codes.CANNOT_ACCEPT.code
                closeReason.message shouldBe expectedErrorMessage

                collectPlaybacksJob.join()
            }
        }

        it("should only return records within the given proximity") {
            // original playbacks are out of user's reach
            val latitude = 60.0
            val longitude = 60.0
            val newPlayback =
                Playback.getTestInstance().copy(
                    location =
                        Location(
                            latitude = latitude,
                            longitude = longitude,
                        ),
                )

            connectToPlaybackServer(request = {
                url {
                    parameters[QueryParams.LATITUDE] = latitude.toString()
                    parameters[QueryParams.LONGITUDE] = longitude.toString()
                }
            }) {
                val collectPlaybacksJob =
                    launch {
                        for (frame in incoming) {
                            val playback = Json.decodeFromString<Playback>((frame as Frame.Text).readText())
                            receivedPlaybacks += playback
                        }
                    }
                (playbacks + listOf(newPlayback)).forEach { mockPlaybackFlow.emit(it) }

                // only new record was polled
                eventually(1.minutes) { receivedPlaybacks.size shouldBe 1 }

                close(CloseReason(CloseReason.Codes.NORMAL, "timeout reached"))
                collectPlaybacksJob.join()
            }

            receivedPlaybacks shouldContainOnly listOf(newPlayback)
        }
    })
