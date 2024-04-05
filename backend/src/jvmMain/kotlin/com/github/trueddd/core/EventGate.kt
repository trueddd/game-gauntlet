package com.github.trueddd.core

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.globalState
import org.jetbrains.annotations.TestOnly

interface EventGate {

    val stateHolder: StateHolder
    val eventManager: EventManager
    val historyHolder: EventHistoryHolder

    @TestOnly
    fun getInputParser(): InputParser

    suspend fun parseAndHandle(input: String): Boolean

    suspend fun start()

    @TestOnly
    fun startNoLoad(initialState: GlobalState = globalState())

    fun stop()

    fun resetState()
}
