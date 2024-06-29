package com.github.trueddd

import com.github.trueddd.plugins.configureRouting
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            authentication {
                // Installing auth plugin for private routes
                basic {
                }
            }
            configureRouting()
        }
        client.get("/icons/1.png").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
