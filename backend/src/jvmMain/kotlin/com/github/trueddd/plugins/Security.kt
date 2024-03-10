package com.github.trueddd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.github.trueddd.core.StateHolder
import com.github.trueddd.utils.Environment
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.days

object Jwt {
    const val AUDIENCE = "JWT_AUDIENCE"
    const val REALM = "JWT_REALM"
    const val SECRET = "JWT_SECRET"
    const val DOMAIN = "JWT_DOMAIN"
    val Verifier: JWTVerifier by lazy {
        val config = Environment.resolveConfig()
        JWT
            .require(Algorithm.HMAC256(config.getProperty(SECRET)))
            .withAudience(config.getProperty(AUDIENCE))
            .withIssuer(config.getProperty(DOMAIN))
            .build()
    }
}

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
