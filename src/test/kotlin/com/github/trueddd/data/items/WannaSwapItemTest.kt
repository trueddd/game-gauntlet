package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.GameRoll
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import com.github.trueddd.data.Game
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WannaSwapItemTest : EventGateTest() {

    @Test
    fun `games swap approved`() = runTest {
        val (user1, user2) = requireParticipants()
        handleAction(BoardMove(user1, diceValue = 2))
        handleAction(BoardMove(user2, diceValue = 3))
        handleAction(GameRoll(user1, Game.Id(0)))
        handleAction(GameRoll(user2, Game.Id(1)))
        val item = WannaSwap.create()
        handleAction(ItemReceive(user1, item))
        handleAction(ItemUse(user1, item.uid, listOf(user2.name)))
        assertEquals(Game.Id(1), lastGameOf(user1)?.game?.id)
        assertEquals(Game.Id(0), lastGameOf(user2)?.game?.id)
        assertTrue(pendingEventsOf(user1).isEmpty())
    }
}
