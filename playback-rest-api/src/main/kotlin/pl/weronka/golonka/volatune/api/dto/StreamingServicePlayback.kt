package pl.weronka.golonka.volatune.dto

import kotlinx.serialization.Serializable
import pl.weronka.golonka.volatune.common.domain.Location
import pl.weronka.golonka.volatune.common.domain.Playback
import pl.weronka.golonka.volatune.common.domain.User

@Serializable
sealed class StreamingServicePlayback {
    abstract val timestamp: Long
    abstract val item: StreamingServicePlaybackItem

    abstract fun toPlayback(
        user: User,
        location: Location,
    ): Playback

    @Serializable
    data class Spotify(
        override val timestamp: Long,
        override val item: StreamingServicePlaybackItem,
    ) : StreamingServicePlayback() {
        override fun toPlayback(
            user: User,
            location: Location,
        ): Playback =
            Playback(
                user = user,
                item = item.toPlaybackItem(),
                timestamp = timestamp,
                location = location,
            )
    }
}
