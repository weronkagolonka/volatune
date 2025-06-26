package pl.weronkagolonka.volatune.domain.events

import kotlinx.serialization.Serializable
import pl.weronkagolonka.volatune.domain.Geohash
import pl.weronkagolonka.volatune.domain.Song
import pl.weronkagolonka.volatune.domain.User

@Serializable
data class Playback(
    val user: User?,
    val song: Song,
    val timestamp: Long,
    val location: Geohash,
)
