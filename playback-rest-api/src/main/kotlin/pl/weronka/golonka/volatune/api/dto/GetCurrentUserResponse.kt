package pl.weronka.golonka.volatune.dto

import kotlinx.serialization.Serializable
import pl.weronka.golonka.volatune.common.domain.User

@Serializable
data class GetCurrentUserResponse(
    val user: User,
)
