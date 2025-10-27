package pl.weronka.golonka.volatune.dto

import kotlinx.serialization.Serializable
import pl.weronka.golonka.volatune.common.domain.PlaybackItem

@Serializable
data class GetCurrentPlaybackResponse(
    val playbackItem: PlaybackItem,
)
