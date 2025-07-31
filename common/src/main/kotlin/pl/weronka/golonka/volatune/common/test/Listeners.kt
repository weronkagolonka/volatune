package pl.weronka.golonka.volatune.common.test

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import org.apache.avro.Schema
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
import org.junit.jupiter.api.fail
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.collections.filterNot
import kotlin.collections.map

class KafkaCleanerListener(
    val bootstrapServers: String,
) : TestListener {
    private lateinit var admin: AdminClient

    override suspend fun beforeSpec(spec: Spec) {
        admin =
            AdminClient.create(
                mapOf(
                    AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ),
            )
    }

    override suspend fun beforeTest(testCase: TestCase) {
        admin.listTopics().names().get().filterNot { it.startsWith("_") }.let {
            if (it.isNotEmpty()) {
                admin.deleteTopics(it).all().get()
            }
        }

        admin.listConsumerGroups().all().get().map { it.groupId() }.let {
            if (it.isNotEmpty()) {
                admin.deleteConsumerGroups(it).all().get()
            }
        }
    }

    override suspend fun afterSpec(spec: Spec) {
        admin.close()
    }
}

class SchemaRegistererListener(
    val registryUrl: String,
    val topic: String,
    val schema: Schema,
) : TestListener {
    override suspend fun beforeSpec(spec: Spec) {
        val subject = "$topic-value"
        val body =
            """
            {"schema":${Json.encodeToString(JsonPrimitive(schema.toString()))}}
            """.trimIndent()
        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("$registryUrl/subjects/$subject/versions"))
                .header("Content-Type", "application/vnd.schemaregistry.v1+json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build()

        val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200) {
            fail { "Could not register the schema ${schema.javaClass.simpleName} for topic $topic" }
        }
    }
}
