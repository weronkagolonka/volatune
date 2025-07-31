package pl.weronka.golonka.volatune.common.domain.config

import com.sksamuel.hoplite.ConfigException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import pl.weronka.golonka.volatune.common.config.loadConfiguration
import java.time.Duration

data class TestConfig(
    val stringProperty: String,
    val numberProperty: Int,
    val durationProperty: Duration,
)

class LoadConfigurationTest :
    DescribeSpec({
        it("should correctly load the config and take precedence of application.conf") {
            val expectedConfig =
                TestConfig(
                    stringProperty = "test",
                    numberProperty = 2,
                    durationProperty = Duration.ofSeconds(1),
                )
            val actualConfig = loadConfiguration<TestConfig>()

            actualConfig shouldBe expectedConfig
        }

        it("should throw an exception if file is invalid") {
            shouldThrow<ConfigException> {
                loadConfiguration<TestConfig>(applicationConfFile = "invalid.conf")
            }
        }

        it("should throw an exception if config files are missing") {
            shouldThrow<ConfigException> {
                loadConfiguration<TestConfig>(
                    referenceConfFile = "non-existent-reference.conf",
                    applicationConfFile = "non-existent-application.conf",
                )
            }
        }
    })
