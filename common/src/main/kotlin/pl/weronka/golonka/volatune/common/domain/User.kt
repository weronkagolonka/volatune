package pl.weronkagolonka.volatune.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String? = null,
    val profileImageUrl: String? = null,
)
