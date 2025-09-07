package pl.weronka.golonka.volatune.websocket.server

import io.ktor.server.websocket.WebSocketServerSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close

object QueryParams {
    const val LATITUDE = "lat"
    const val LONGITUDE = "long"
}

suspend fun WebSocketServerSession.latitudeQueryParam(): Double? =
    getQueryParamValue(QueryParams.LATITUDE) {
        it?.toDouble()
    }

suspend fun WebSocketServerSession.longitudeQueryParam(): Double? =
    getQueryParamValue(QueryParams.LONGITUDE) {
        it?.toDouble()
    }

private suspend fun <T> WebSocketServerSession.getQueryParamValue(
    key: String,
    parseFn: (queryParamValue: String?) -> T?,
): T? =
    runCatching {
        val param = call.request.queryParameters[key]
        parseFn(param)
    }.fold({ param ->
        if (param == null) {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "$key query parameter is required"))
            return null
        }
        return param
    }, { error ->
        close(
            CloseReason(
                CloseReason.Codes.CANNOT_ACCEPT,
                error.message ?: "Unexpected error when retrieving query parameter $key",
            ),
        )
        return null
    })
