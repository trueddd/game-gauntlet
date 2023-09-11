package com.github.trueddd.core.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.GameStatusChange
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.data.items.NoClownery
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NoClowneryItemTest : EventGateTest() {

    @Test
    fun `item dispose - after special spot`() = runTest {
        val user = requireParticipant("shizov")
        val item = NoClownery.create()
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, item))
        assertEquals(expected = 1, effectsOf(user).count { it is NoClownery })
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 4))
        eventGate.eventManager.suspendConsumeAction(GameRoll(user, Game.Id(1)))
        eventGate.eventManager.suspendConsumeAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = 1, effectsOf(user).count { it is NoClownery })
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, 4))
        assertEquals(expected = 0, effectsOf(user).size)
    }
}
