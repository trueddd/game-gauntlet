package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.ThereIsGiftAtYourDoor
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ThereIsGiftAtYourDoorItemTest : EventGateTest() {

    @Test
    fun `leave a gift for myself`() = runTest {
        val user = requireRandomParticipant()
        val item = ThereIsGiftAtYourDoor.create()
        handleAction(BoardMove(user, diceValue = 3))
        handleAction(ItemReceive(user, item))
        handleAction(ItemUse(user, item, user.name))
        assertEquals(expected = 1, effectsOf(user).size)
        assertEquals(expected = 0, pendingEventsOf(user).size)
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(BoardMove(user, diceValue = 2))
        assertEquals(expected = 3, positionOf(user))
        assertEquals(expected = 0, effectsOf(user).size)
    }

    @Test
    fun `throw a gift at opponent`() = runTest {
        val (user1, user2) = requireParticipants()
        val item = ThereIsGiftAtYourDoor.create()
        handleAction(ItemReceive(user1, item))
        handleAction(ItemUse(user1, item, user2.name))
        assertEquals(expected = 1, effectsOf(user2).size)
        assertEquals(expected = -2, stateOf(user1).modifiersSum)
        assertEquals(expected = 0, pendingEventsOf(user1).size)
        handleAction(BoardMove(user2, diceValue = 3))
        handleAction(GameRoll(user2, Game.Id(2)))
        handleAction(GameStatusChange(user2, Game.Status.Finished))
        handleAction(BoardMove(user2, diceValue = 2))
        handleAction(GameRoll(user2, Game.Id(3)))
        assertEquals(expected = 3, positionOf(user2))
        assertEquals(expected = 0, effectsOf(user2).size)
        assertEquals(expected = 3, lastGameOf(user2)?.game?.id?.value)
    }
}
