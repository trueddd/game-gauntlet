package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.GameSet
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.data.Game
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DontUnderstandItemTest : EventGateTest() {

    @Test
    fun `get game with item`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, DontUnderstand.create()))
        assertTrue(effectsOf(user).isNotEmpty())
        handleAction(GameSet(user, Game.Id(3)))
        assertTrue(effectsOf(user).isEmpty())
        assertEquals(expected = 1, stateOf(user).gameHistory.size)
    }
}
