import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_23)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

dependencies {
    implementation(libs.geohash)

    implementation(libs.avro4k)
    implementation(libs.avro.serializer)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlin.coroutines.core)

    implementation(libs.kafka.clients)

    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.bundles.kotest)
}
