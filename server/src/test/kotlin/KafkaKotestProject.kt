import io.kotest.core.config.AbstractProjectConfig
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.kafka.ConfluentKafkaContainer
import org.testcontainers.utility.DockerImageName
import kotlin.getValue

private const val KAFKA_PORT = 19092
private const val KAFKA_HOST = "kafka"

object TestContainers {
    private val network = Network.newNetwork()

    val kafka: ConfluentKafkaContainer by lazy {
        ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.2"))
            .withNetwork(network)
            .withListener("$KAFKA_HOST:$KAFKA_PORT")
            .also {
                it.start()
            }
    }

    val schemaRegistry: GenericContainer<*> by lazy {
        GenericContainer(DockerImageName.parse("confluentinc/cp-schema-registry:7.5.2"))
            .withNetwork(network)
            .withExposedPorts(8081)
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
            .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
            .withEnv(
                "SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS",
                "PLAINTEXT://${KAFKA_HOST}:${KAFKA_PORT}",
            ).withEnv("SCHEMA_REGISTRY_KAFKASTORE_SECURITY_PROTOCOL", "PLAINTEXT")
            .waitingFor(Wait.forHttp("/subjects").forStatusCode(200))
            .also {
                it.start()
            }
    }
}

object KafkaKotestProject : AbstractProjectConfig() {
    override suspend fun beforeProject() {
        TestContainers.kafka
        println("Kafka started")
        TestContainers.schemaRegistry
        println("Schema registry started")
    }

    override suspend fun afterProject() {
        TestContainers.schemaRegistry.stop()
        TestContainers.kafka.stop()
    }
}
