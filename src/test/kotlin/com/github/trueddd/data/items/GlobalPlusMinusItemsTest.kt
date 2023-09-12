package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.utils.rollDice
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GlobalPlusMinusItemsTest : EventGateTest() {

    @Test
    fun `plus to everyone`() = runTest {
        eventGate.stateHolder.current.players.values.forEach { player ->
            assertEquals(expected = 0, player.modifiersSum)
        }
        handleAction(ItemReceive(eventGate.stateHolder.current.players.keys.random(), PlusToEveryone.create()))
        eventGate.stateHolder.current.players.values.forEach { player ->
            assertEquals(expected = 1, player.modifiersSum)
        }
    }

    @Test
    fun `plus to everyone - ensure one move only`() = runTest {
        handleAction(ItemReceive(eventGate.stateHolder.current.players.keys.random(), PlusToEveryone.create()))
        eventGate.stateHolder.current.players.values.forEach { player ->
            assertEquals(expected = 1, player.modifiersSum)
        }
        eventGate.stateHolder.current.players.keys.forEach { player ->
            handleAction(BoardMove(player, rollDice()))
        }
        eventGate.stateHolder.current.players.values.forEach { player ->
            assertEquals(expected = 0, player.modifiersSum)
        }
    }

    @Test
    fun `minus to everyone`() = runTest {
        eventGate.stateHolder.current.players.values.forEach { player ->
            assertEquals(expected = 0, player.modifiersSum)
        }
        handleAction(ItemReceive(eventGate.stateHolder.current.players.keys.random(), MinusToEveryone.create()))
        eventGate.stateHolder.current.players.values.forEach { player ->
            assertEquals(expected = -1, player.modifiersSum)
        }
    }

    @Test
    fun `minus to everyone - ensure one move only`() = runTest {
        handleAction(ItemReceive(eventGate.stateHolder.current.players.keys.random(), MinusToEveryone.create()))
        eventGate.stateHolder.current.players.values.forEach { player ->
            assertEquals(expected = -1, player.modifiersSum)
        }
        eventGate.stateHolder.current.players.keys.forEachIndexed { index, player ->
            handleAction(BoardMove(player, diceValue = index * 2 + 1))
        }
        eventGate.stateHolder.current.players.values.forEach { player ->
            if (player.position == 1) {
                assertEquals(expected = -1, player.modifiersSum)
            } else {
                assertEquals(expected = 0, player.modifiersSum)
            }
        }
    }

    @Test
    fun `minus to everyone but you`() = runTest {
        val player = requireParticipant("shizov")
        eventGate.stateHolder.current.players.values.forEach {
            assertEquals(expected = 0, it.modifiersSum)
        }
        handleAction(ItemReceive(player, MinusToEveryoneButYou.create()))
        eventGate.stateHolder.current.players.forEach { (participant, state) ->
            if (player == participant) {
                assertEquals(expected = 1, state.modifiersSum)
            } else {
                assertEquals(expected = -1, state.modifiersSum)
            }
        }
    }

    @Test
    fun `plus to everyone but you`() = runTest {
        val player = requireParticipant("shizov")
        eventGate.stateHolder.current.players.values.forEach {
            assertEquals(expected = 0, it.modifiersSum)
        }
        handleAction(ItemReceive(player, PlusToEveryoneButYou.create()))
        eventGate.stateHolder.current.players.forEach { (participant, state) ->
            if (player == participant) {
                assertEquals(expected = -1, state.modifiersSum)
            } else {
                assertEquals(expected = 1, state.modifiersSum)
            }
        }
    }
}
