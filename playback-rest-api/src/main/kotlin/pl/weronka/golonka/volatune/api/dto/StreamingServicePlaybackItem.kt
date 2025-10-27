package pl.weronka.golonka.volatune.dto

import kotlinx.serialization.Serializable
import pl.weronka.golonka.volatune.common.domain.PlaybackItem
import pl.weronka.golonka.volatune.dto.metadata.SpotifyAlbum
import pl.weronka.golonka.volatune.dto.metadata.SpotifyArtist
import pl.weronka.golonka.volatune.dto.metadata.SpotifyImage
import pl.weronka.golonka.volatune.dto.metadata.SpotifyShow

@Serializable
sealed class StreamingServicePlaybackItem {
    abstract val type: String

    abstract fun toPlaybackItem(): PlaybackItem

    init {
        require(this.type.isNotBlank()) { "Type property cannot be blank" }
    }

    @Serializable
    data class SpotifySong(
        val album: SpotifyAlbum,
        val artists: List<SpotifyArtist>,
        val id: String,
        val name: String,
        val timestamp: Int,
    ) : StreamingServicePlaybackItem() {
        override val type = "track"

        override fun toPlaybackItem(): PlaybackItem =
            PlaybackItem.Song(
                title = this.name,
                artists = this.artists.map { it.name },
                album = this.album.toAlbum(),
                timestamp = this.timestamp.toLong(),
            )
    }

    @Serializable
    data class SpotifyPodcast(
        val id: String,
        val images: List<SpotifyImage>,
        val name: String,
        val show: SpotifyShow,
        val timestamp: Int,
    ) : StreamingServicePlaybackItem() {
        override val type: String = "episode"

        override fun toPlaybackItem(): PlaybackItem =
            PlaybackItem.Podcast(
                title = this.name,
                show = this.show.name,
                coverImage = this.images.firstOrNull()?.toImage(),
                timestamp = this.timestamp.toLong(),
            )
    }
}
