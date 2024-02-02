package com.github.trueddd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.trueddd.utils.Environment
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

object Jwt {
    const val AUDIENCE = "JWT_AUDIENCE"
    const val REALM = "JWT_REALM"
    const val SECRET = "JWT_SECRET"
    const val DOMAIN = "JWT_DOMAIN"
}

fun Application.configureSecurity() {
    authentication {
        jwt {
            val config = Environment.resolveConfig("jwt.properties")
            val jwtAudience = config.getProperty(Jwt.AUDIENCE)
            realm = config.getProperty(Jwt.REALM)
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.getProperty(Jwt.SECRET)))
                    .withAudience(jwtAudience)
                    .withIssuer(config.getProperty(Jwt.DOMAIN))
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
