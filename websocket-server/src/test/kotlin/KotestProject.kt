import io.kotest.core.config.AbstractProjectConfig
import pl.weronka.golonka.volatune.common.test.TestContainers

object KotestProject : AbstractProjectConfig() {
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
