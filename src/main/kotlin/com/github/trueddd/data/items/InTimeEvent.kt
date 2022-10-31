package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant

interface InTimeEvent {
    suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState
}
