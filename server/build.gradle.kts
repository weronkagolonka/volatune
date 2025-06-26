plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(libs.geohash)

    implementation(libs.avro4k)
    implementation(libs.kotlinx.serialization)

    implementation(libs.kafka.clients)

    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.kafka)
    testImplementation(kotlin("test"))
}
