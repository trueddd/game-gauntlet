package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.data.Participant
import com.github.trueddd.data.items.DropReverse
import com.github.trueddd.data.items.SamuraiLunge
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DropGame : EventGateTest() {

    @Test
    fun `drop game with SamuraiLunge`() = runTest {
        val user = Participant("solll")
        val moveDiceValue = 5
        val dropDiceValue = 4
        eventGate.eventManager.suspendConsumeAction(BoardMove(user, moveDiceValue))
        eventGate.parseAndHandleSuspend("${Action.Commands.GAME_ROLL} ${user.name}")
        val item = SamuraiLunge.create()
        eventGate.eventManager.suspendConsumeAction(ItemReceive(user, item))
        eventGate.eventManager.suspendConsumeAction(ItemUse(user, item.uid))
        eventGate.eventManager.suspendConsumeAction(GameDrop(user, dropDiceValue))
        assertTrue(eventGate.stateHolder.current.players[user]!!.inventory.isEmpty())
        assertTrue(eventGate.stateHolder.current.players[user]!!.effects.none { it is DropReverse })
        assertEquals(Game.Status.Dropped, eventGate.stateHolder.current.players[user]?.currentGameEntry?.status)
        assertEquals(moveDiceValue + dropDiceValue, eventGate.stateHolder.current.players[user]!!.position)
    }
}
