plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(project(":common"))

    implementation(libs.avro4k)

    implementation(libs.kafka.clients)

    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlinx.serialization)

    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.testcontainers)
}
