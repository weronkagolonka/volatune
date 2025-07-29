package pl.weronka.golonka.volatune.common.domain.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource

fun <T> loadConfiguration(): T =
    ConfigLoaderBuilder
        .default()
        .addResourceSource("/application.conf")
        .build()
        .loadConfigOrThrow()
