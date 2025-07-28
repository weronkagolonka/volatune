package pl.weronkagolonka.volatune.domain

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String,
    val title: String,
    val artists: List<String>,
    val album: String,
    val albumImageUrl: String,
)
