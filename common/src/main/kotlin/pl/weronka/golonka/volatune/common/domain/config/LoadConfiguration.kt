package pl.weronka.golonka.volatune.common.domain.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource

@OptIn(ExperimentalHoplite::class)
inline fun <reified T : Any> loadConfiguration(
    referenceConfFile: String = "reference.conf",
    applicationConfFile: String = "application.conf",
): T =
    ConfigLoaderBuilder
        .default()
        .addResourceSource("/$applicationConfFile")
        .addResourceSource("/$referenceConfFile", true)
        .withExplicitSealedTypes("type")
        .build()
        .loadConfigOrThrow<T>()
