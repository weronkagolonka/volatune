package pl.weronka.golonka.volatune.common.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class PlaybackItem {
    abstract val title: String

    @Serializable
    data class Song(
        override val title: String,
        val artists: List<String>,
        val album: Album,
    ) : PlaybackItem()

    @Serializable
    data class Podcast(
        override val title: String,
        val show: String,
        val coverImage: Image?,
    ) : PlaybackItem()
}

@Serializable
data class Album(
    val title: String,
    val coverImage: Image?,
)
