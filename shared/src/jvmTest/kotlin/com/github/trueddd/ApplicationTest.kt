package com.github.trueddd

import com.github.trueddd.plugins.configureRouting
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/icons/1.png").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}