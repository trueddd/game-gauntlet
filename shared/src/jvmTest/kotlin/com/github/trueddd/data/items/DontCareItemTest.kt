package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.DontCare
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DontCareItemTest : EventGateTest() {

    @Test
    fun `set game successfully - 1`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, DontCare.create()))
        handleAction(GameSet(user, Game.Id(1)))
        assertEquals(expected = Game.Id(1), stateOf(user).currentActiveGame?.game?.id)
    }

    @Test
    fun `set game successfully - 2`() = runTest {
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 3))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(ItemReceive(user, DontCare.create()))
        handleAction(GameSet(user, Game.Id(1)))
        assertEquals(expected = Game.Status.Next, stateOf(user).gameHistory.lastOrNull()?.status)
        assertEquals(expected = Game.Id(2), stateOf(user).currentActiveGame?.game?.id)
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = Game.Id(1), stateOf(user).currentActiveGame?.game?.id)
    }

    @Test
    fun `set game with failure - 1`() = runTest {
        val user = requireRandomParticipant()
        handleAction(BoardMove(user, diceValue = 3))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameSet(user, Game.Id(1)))
        assertEquals(expected = Game.Id(2), stateOf(user).currentActiveGame?.game?.id)
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = Game.Id(2), stateOf(user).currentGame?.game?.id)
    }
}
