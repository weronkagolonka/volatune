package pl.weronka.golonka.volatune.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val title: String,
    val status: Int,
    val detail: String? = null,
)
