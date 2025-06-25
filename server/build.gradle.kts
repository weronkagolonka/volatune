plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kafka.clients)

    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.kafka)
    testImplementation(kotlin("test"))
}
