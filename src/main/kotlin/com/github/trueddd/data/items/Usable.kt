package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant

interface Usable {
    suspend fun use(usedBy: Participant, globalState: GlobalState): GlobalState
}
