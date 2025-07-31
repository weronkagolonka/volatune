plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(project(":common"))
    implementation(project(":kafka"))

    implementation(libs.avro4k)
    implementation(libs.kotlinx.serialization)

    implementation(libs.ktor.dependency.injection)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.testcontainers)
}
