package pl.weronka.golonka.volatune.api.routes

import io.ktor.server.auth.BearerTokenCredential
import io.ktor.server.auth.principal
import io.ktor.server.routing.RoutingContext
import pl.weronka.golonka.volatune.Paths
import pl.weronka.golonka.volatune.api.exceptions.StreamingProviderException
import pl.weronka.golonka.volatune.api.exceptions.UnauthorizedException
import pl.weronka.golonka.volatune.common.domain.dto.StreamingProvider

fun RoutingContext.streamingProvider(): StreamingProvider =
    runCatching {
        call.parameters[Paths.PROVIDER]?.let { provider ->
            StreamingProvider.valueOf(provider)
        } ?: throw StreamingProviderException("Provider not present in path parameters")
    }.getOrElse {
        throw StreamingProviderException("Failed to extract the provider from path parameters", it)
    }

fun RoutingContext.bearerToken(): String = call.principal<BearerTokenCredential>()?.token ?: throw UnauthorizedException()
