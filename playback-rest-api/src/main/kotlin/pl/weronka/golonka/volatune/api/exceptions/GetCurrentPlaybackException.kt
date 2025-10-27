package pl.weronka.golonka.volatune.exceptions

class GetCurrentPlaybackException(
    val className: String,
    val statusCode: Int? = null,
    override val cause: Throwable? = null,
) : RuntimeException("Failed to get the current playback from the $className.", cause)
