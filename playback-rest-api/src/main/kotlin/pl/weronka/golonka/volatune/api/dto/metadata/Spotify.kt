package pl.weronka.golonka.volatune.dto.metadata

import kotlinx.serialization.Serializable
import pl.weronka.golonka.volatune.common.domain.Album
import pl.weronka.golonka.volatune.common.domain.Image

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
