package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import com.github.trueddd.data.Game
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DontWannaPlayThisItemTest : EventGateTest() {

    @Test
    fun `reroll game with item`() = runTest {
        val user = requireParticipant("shizov")
        val item = DontWannaPlayThis.create()
        handleAction(BoardMove(user, 5))
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemUse(user, item.uid))
        assertEquals(expected = 0, inventoryOf(user).size)
        assertEquals(expected = 0, effectsOf(user).size)
        assertEquals(expected = 1, eventGate.stateHolder[user].gameHistory.size)
        assertEquals(expected = Game.Status.Rerolled, eventGate.stateHolder[user].currentGame?.status)
    }
}
