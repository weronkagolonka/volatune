package pl.weronka.golonka.volatune.common.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class PlaybackItem {
    abstract val title: String
    abstract val timestamp: Long

    @Serializable
    data class Song(
        override val title: String,
        override val timestamp: Long,
        val artists: List<String>,
        val album: Album,
    ) : PlaybackItem()

    @Serializable
    data class Podcast(
        override val title: String,
        override val timestamp: Long,
        val show: String,
        val coverImage: Image?,
    ) : PlaybackItem()
}

@Serializable
data class Album(
    val title: String,
    val coverImage: Image?,
)
