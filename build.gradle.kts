plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    java
}

allprojects {
    group = "pl.weronkagolonka.volatune"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}
