package pl.weronka.golonka.volatune.pl.weronka.golonka.volatune.dto.providers

import kotlinx.serialization.Serializable
import pl.weronka.golonka.volatune.common.domain.Image
import pl.weronkagolonka.volatune.domain.Album

@Serializable
data class SpotifySongItem(
    val album: SpotifyAlbum,
    val artists: List<SpotifyArtist>,
    val id: String,
    val name: String,
)

@Serializable
data class SpotifyAlbum(
    val images: List<SpotifyImage>,
    val name: String,
) {
    fun toAlbum() =
        Album(
            this.name,
            images.firstOrNull()?.toImage(),
        )
}

@Serializable
data class SpotifyImage(
    val url: String,
    val height: Int,
    val width: Int,
) {
    fun toImage(): Image =
        Image(
            url = this.url,
            height = this.height,
            width = this.width,
        )
}

@Serializable
data class SpotifyArtist(
    val name: String,
)

@Serializable
data class SpotifyShow(
    val name: String,
)
