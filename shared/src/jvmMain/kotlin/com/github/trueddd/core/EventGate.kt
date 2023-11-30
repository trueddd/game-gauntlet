package com.github.trueddd.core

import org.jetbrains.annotations.TestOnly

interface EventGate {

    val stateHolder: StateHolder
    val eventManager: EventManager
    val historyHolder: EventHistoryHolder

    @TestOnly
    fun getInputParser(): InputParser

    suspend fun parseAndHandle(input: String): Boolean

    @TestOnly
    suspend fun parseAndHandleSuspend(input: String): Boolean

    fun start()

    fun stop()
}
