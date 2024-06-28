package com.github.trueddd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.trueddd.core.StateHolder
import com.github.trueddd.plugins.security.Jwt
import com.github.trueddd.utils.Environment
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication
import io.ktor.server.auth.principal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.jwt.JWTPrincipal
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.days

fun createJwtToken(claim: String): String {
    val config = Environment.resolveConfig()
    return JWT.create()
        .withAudience(config.getProperty(Jwt.AUDIENCE))
        .withIssuer(config.getProperty(Jwt.DOMAIN))
        .withClaim("user", claim)
        .withExpiresAt((Clock.System.now() + 7.days).toJavaInstant())
        .sign(Algorithm.HMAC256(config.getProperty(Jwt.SECRET)))
}

fun Application.configureSecurity() {
    val stateHolder by inject<StateHolder>()
    authentication {
        jwt {
            realm = Environment.resolveConfig().getProperty(Jwt.REALM)
            verifier(Jwt.Verifier)
            validate { credential ->
                val user = credential.payload.getClaim("user").asString()
                if (user in stateHolder.participants.map { it.name }) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}

val ApplicationCall.userLogin: String?
    get() = principal<JWTPrincipal>()?.get("user")
