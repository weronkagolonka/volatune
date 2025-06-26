package pl.weronkagolonka.volatune.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val profileImageUrl: String,
)
