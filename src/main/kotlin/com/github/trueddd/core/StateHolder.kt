package com.github.trueddd.core

import com.github.trueddd.data.GlobalState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single

@Single
class StateHolder {

    private val _globalStateFlow = MutableStateFlow(GlobalState.default())
    val globalStateFlow = _globalStateFlow.asStateFlow()

    fun update(block: GlobalState.() -> GlobalState) = _globalStateFlow.update(block)
}
