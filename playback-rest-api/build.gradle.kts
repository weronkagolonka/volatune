plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktor)
}

dependencies {
    implementation(project(":common"))

    implementation(libs.bundles.ktor)
}
