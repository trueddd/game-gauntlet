package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant

interface Usable {
    /**
     * This method uses `this` item. Charges, if presented, should be decremented here,
     * and if this item is one-time usable, it should be removed from the inventory here.
     */
    suspend fun use(usedBy: Participant, globalState: GlobalState): GlobalState
}
