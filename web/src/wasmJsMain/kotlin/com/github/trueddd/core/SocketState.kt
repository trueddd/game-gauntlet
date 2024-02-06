package com.github.trueddd.core

sealed class SocketState {

    class Disconnected(error: Error? = null) : SocketState()

    data object Connecting : SocketState()

    data object Connected : SocketState()
}
