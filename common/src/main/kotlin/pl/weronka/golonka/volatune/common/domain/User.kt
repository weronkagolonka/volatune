package pl.weronka.golonka.volatune.common.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String? = null,
    val image: Image? = null,
)
