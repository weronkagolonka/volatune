package pl.weronka.golonka.volatune.pl.weronka.golonka.volatune.exceptions

class GetCurrentUserException(
    val className: String,
    val statusCode: Int? = null,
    override val cause: Throwable? = null,
) : RuntimeException("Failed to get the current user from the $className.", cause)
