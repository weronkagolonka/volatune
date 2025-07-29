plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(libs.geohash)

    implementation(libs.avro4k)
    implementation(libs.kotlinx.serialization)

    implementation(libs.kafka.clients)

    implementation(libs.bundles.hoplite)

    testImplementation(libs.bundles.kotest)
}
