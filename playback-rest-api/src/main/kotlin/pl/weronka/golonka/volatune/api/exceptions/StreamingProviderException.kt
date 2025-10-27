package pl.weronka.golonka.volatune.api.exceptions

class StreamingProviderException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)
