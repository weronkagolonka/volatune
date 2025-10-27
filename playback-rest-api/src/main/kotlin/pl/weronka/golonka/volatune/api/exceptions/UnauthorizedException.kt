package pl.weronka.golonka.volatune.api.exceptions

class UnauthorizedException :
    RuntimeException(
        "Bearer token is required",
    )
