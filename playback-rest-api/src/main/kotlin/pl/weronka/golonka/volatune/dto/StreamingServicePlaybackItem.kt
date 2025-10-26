package pl.weronka.golonka.volatune.pl.weronka.golonka.volatune.dto

import kotlinx.serialization.Serializable
import pl.weronka.golonka.volatune.common.domain.PlaybackItem
import pl.weronka.golonka.volatune.pl.weronka.golonka.volatune.dto.providers.SpotifyAlbum
import pl.weronka.golonka.volatune.pl.weronka.golonka.volatune.dto.providers.SpotifyArtist
import pl.weronka.golonka.volatune.pl.weronka.golonka.volatune.dto.providers.SpotifyImage
import pl.weronka.golonka.volatune.pl.weronka.golonka.volatune.dto.providers.SpotifyShow

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
    ) : StreamingServicePlaybackItem() {
        override val type = "track"

        override fun toPlaybackItem(): PlaybackItem =
            PlaybackItem.Song(
                title = this.name,
                artists = this.artists.map { it.name },
                album = this.album.toAlbum(),
            )
    }

    @Serializable
    data class SpotifyPodcast(
        val id: String,
        val images: List<SpotifyImage>,
        val name: String,
        val show: SpotifyShow,
    ) : StreamingServicePlaybackItem() {
        override val type: String = "episode"

        override fun toPlaybackItem(): PlaybackItem =
            PlaybackItem.Podcast(
                title = this.name,
                show = this.show.name,
                coverImage = images.firstOrNull()?.toImage(),
            )
    }
}
