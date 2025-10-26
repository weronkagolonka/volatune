package pl.weronka.golonka.volatune.pl.weronka.golonka.volatune.dto

import kotlinx.serialization.Serializable
import pl.weronka.golonka.volatune.common.domain.Image
import pl.weronka.golonka.volatune.common.domain.User
import pl.weronka.golonka.volatune.pl.weronka.golonka.volatune.dto.providers.SpotifyImage

@Serializable
sealed class StreamingServiceUser {
    abstract fun toUser(): User
}

@Serializable
data class SpotifyUser(
    val displayName: String?,
    val images: List<SpotifyImage>,
    val id: String,
) : StreamingServiceUser() {
    override fun toUser(): User =
        User(
            id = this.id,
            name = this.displayName,
            image =
                this.images.firstOrNull()?.let {
                    Image(
                        url = it.url,
                        height = it.height,
                        width = it.width,
                    )
                },
        )
}
