package com.github.trueddd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    authentication {
        jwt {
            val jwtAudience = System.getenv("JWT_AUDIENCE").toString()
            realm = System.getenv("JWT_REALM").toString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(System.getenv("JWT_SECRET").toString()))
                    .withAudience(jwtAudience)
                    .withIssuer(System.getenv("JWT_DOMAIN").toString())
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
