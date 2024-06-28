package com.github.trueddd.plugins.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.github.trueddd.utils.Environment

object Jwt {
    const val AUDIENCE = "JWT_AUDIENCE"
    const val REALM = "JWT_REALM"
    const val SECRET = "JWT_SECRET"
    const val DOMAIN = "JWT_DOMAIN"
    val Verifier: JWTVerifier by lazy {
        val config = Environment.resolveConfig()
        JWT.require(Algorithm.HMAC256(config.getProperty(SECRET)))
            .withAudience(config.getProperty(AUDIENCE))
            .withIssuer(config.getProperty(DOMAIN))
            .build()
    }
}
