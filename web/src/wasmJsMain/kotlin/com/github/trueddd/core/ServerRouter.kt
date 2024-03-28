package com.github.trueddd.core

import com.github.trueddd.util.isDevEnvironment
import com.github.trueddd.util.serverAddress
import io.ktor.http.*

class ServerRouter {

    private fun base(protocol: URLProtocol, path: String): String {
        return URLBuilder(
            protocol = protocol,
            host = serverAddress().substringBefore(":"),
            port = serverAddress().substringAfter(":").toIntOrNull() ?: DEFAULT_PORT,
            pathSegments = path.split("/").filter { it.isNotEmpty() },
        ).buildString()
    }

    fun ws(path: String): String {
        return base(
            protocol = if (isDevEnvironment()) URLProtocol.WS else URLProtocol.WSS,
            path = path,
        )
    }

    fun http(path: String): String {
        return base(
            protocol = if (isDevEnvironment()) URLProtocol.HTTP else URLProtocol.HTTPS,
            path = path,
        )
    }

    fun wheelItemIconUrl(id: Int) = http("icons/$id.png")
}
