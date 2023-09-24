package com.github.trueddd.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.github.trueddd.utils.Log
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties

object Jwt {
    const val AUDIENCE = "JWT_AUDIENCE"
    const val REALM = "JWT_REALM"
    const val SECRET = "JWT_SECRET"
    const val DOMAIN = "JWT_DOMAIN"
}

fun Application.configureSecurity() {
    authentication {
        jwt {
            val config = readJwtConfig()
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
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

private fun currentDir(): Path {
    return Paths.get(Log::class.java.protectionDomain.codeSource.location.toURI())
}

private fun readJwtConfig(): Properties {
    val propertiesFile = File(currentDir().toFile(), "../jwt.properties")
    return if (propertiesFile.exists()) {
        propertiesFile.inputStream().use { Properties().apply { load(it) } }
    } else {
        System.getenv().toProperties()
    }
}
