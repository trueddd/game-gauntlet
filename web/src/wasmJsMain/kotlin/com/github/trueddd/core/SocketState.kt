package com.github.trueddd.core

sealed class SocketState {

    class Disconnected(error: Throwable? = null) : SocketState()

    data object Connecting : SocketState()

    data object Connected : SocketState()
}
