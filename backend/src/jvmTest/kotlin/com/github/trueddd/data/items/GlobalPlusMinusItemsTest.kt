package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.items.MinusToEveryone
import com.github.trueddd.items.MinusToEveryoneButYou
import com.github.trueddd.items.PlusToEveryone
import com.github.trueddd.items.PlusToEveryoneButYou
import com.github.trueddd.utils.rollDice
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GlobalPlusMinusItemsTest : EventGateTest() {

    @Test
    fun `plus to everyone`() = runTest {
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach { player ->
            assertEquals(expected = 0, player.modifiersSum)
        }
        handleAction(ItemReceive(getRandomPlayerName(), PlusToEveryone.create()))
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach { player ->
            assertEquals(expected = 1, player.modifiersSum)
        }
    }

    @Test
    fun `plus to everyone - ensure one move only`() = runTest {
        handleAction(ItemReceive(getRandomPlayerName(), PlusToEveryone.create()))
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach { player ->
            assertEquals(expected = 1, player.modifiersSum)
        }
        eventGate.stateHolder.current.players.forEach {
            handleAction(BoardMove(it.name, rollDice()))
        }
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach { player ->
            assertEquals(expected = 0, player.modifiersSum)
        }
    }

    @Test
    fun `minus to everyone`() = runTest {
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach { player ->
            assertEquals(expected = 0, player.modifiersSum)
        }
        handleAction(ItemReceive(getRandomPlayerName(), MinusToEveryone.create()))
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach { player ->
            assertEquals(expected = -1, player.modifiersSum)
        }
    }

    @Test
    fun `minus to everyone - ensure one move only`() = runTest {
        handleAction(ItemReceive(getRandomPlayerName(), MinusToEveryone.create()))
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach { player ->
            assertEquals(expected = -1, player.modifiersSum)
        }
        eventGate.stateHolder.current.players.forEachIndexed { index, player ->
            handleAction(BoardMove(player.name, diceValue = if (index == 0) 1 else 3))
        }
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach { player ->
            if (player.position == 1) {
                assertEquals(expected = -1, player.modifiersSum)
            } else {
                assertEquals(expected = 0, player.modifiersSum)
            }
        }
    }

    @Test
    fun `minus to everyone but you`() = runTest {
        val player = getRandomPlayerName()
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach {
            assertEquals(expected = 0, it.modifiersSum)
        }
        handleAction(ItemReceive(player, MinusToEveryoneButYou.create()))
        eventGate.stateHolder.current.stateSnapshot.playersState.forEach { (participant, state) ->
            if (player == participant) {
                assertEquals(expected = 1, state.modifiersSum)
            } else {
                assertEquals(expected = -1, state.modifiersSum)
            }
        }
    }

    @Test
    fun `plus to everyone but you`() = runTest {
        val player = getRandomPlayerName()
        eventGate.stateHolder.current.stateSnapshot.playersState.values.forEach {
            assertEquals(expected = 0, it.modifiersSum)
        }
        handleAction(ItemReceive(player, PlusToEveryoneButYou.create()))
        eventGate.stateHolder.current.stateSnapshot.playersState.forEach { (participant, state) ->
            if (player == participant) {
                assertEquals(expected = -1, state.modifiersSum)
            } else {
                assertEquals(expected = 1, state.modifiersSum)
            }
        }
    }
}
