plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    java
}

allprojects {
    group = "pl.weronkagolonka.volatune"
    version = "1.0.0"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://packages.confluent.io/maven/")
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
