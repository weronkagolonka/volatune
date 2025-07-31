plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {

    implementation(libs.avro4k)
    implementation(libs.kotlinx.serialization)

    implementation(libs.kafka.clients)

    implementation(libs.bundles.hoplite)

    implementation(libs.bundles.testcontainers)
    implementation(libs.bundles.kotest)

    implementation(libs.h3)

    testImplementation(libs.bundles.kotest)
}
