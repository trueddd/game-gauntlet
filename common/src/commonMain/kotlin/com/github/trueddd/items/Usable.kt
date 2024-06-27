package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName

interface Usable {
    /**
     * This method uses `this` item. Charges, if presented, should be decremented here,
     * and if this item is one-time usable, it should be removed from the inventory here.
     */
    suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState
}
