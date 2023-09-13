package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.GameStatusChange
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Game
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class NoClowneryItemTest : EventGateTest() {

    @Test
    fun `item dispose - after special spot`() = runTest {
        val user = requireRandomParticipant()
        val item = NoClownery.create()
        handleAction(ItemReceive(user, item))
        assertEquals(expected = 1, effectsOf(user).count { it is NoClownery })
        handleAction(BoardMove(user, diceValue = 4))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = 1, effectsOf(user).count { it is NoClownery })
        handleAction(BoardMove(user, diceValue = 4))
        assertEquals(expected = 0, effectsOf(user).size)
    }
}
