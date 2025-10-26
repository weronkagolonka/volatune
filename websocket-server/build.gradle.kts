plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(project(":common"))
    implementation(project(":kafka"))

    implementation(libs.avro4k)
    implementation(libs.kotlinx.serialization)

    implementation(libs.bundles.ktor)
    implementation(libs.logback.classic)

    testImplementation(libs.h3)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.mockk)
    testImplementation(libs.bundles.kotest)
}
