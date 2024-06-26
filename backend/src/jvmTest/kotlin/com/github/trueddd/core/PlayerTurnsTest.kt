package com.github.trueddd.core

import com.github.trueddd.actions.*
import com.github.trueddd.data.Game
import com.github.trueddd.items.DontCare
import com.github.trueddd.items.PowerThrow
import com.github.trueddd.provideEventGate
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlayerTurnsTest {

    @Test
    fun `player turns calculation - 1`() = runTest {
        val eventGate = provideEventGate()
        val (player1, player2, player3) = eventGate.stateHolder.participants.map { it.name }
        eventGate.eventManager.startHandling()
        // player 1
        eventGate.eventManager.consumeAction(BoardMove(player1, diceValue = 1))
        val powerThrow = PowerThrow.create()
        eventGate.eventManager.consumeAction(ItemReceive(player1, powerThrow))
        eventGate.eventManager.consumeAction(GameRoll(player1, Game.Id(1)))
        // player 2
        eventGate.eventManager.consumeAction(BoardMove(player2, diceValue = 4))
        eventGate.eventManager.consumeAction(GameRoll(player2, Game.Id(2)))
        // player 3
        eventGate.eventManager.consumeAction(BoardMove(player3, diceValue = 5))
        eventGate.eventManager.consumeAction(GameRoll(player3, Game.Id(2)))
        eventGate.eventManager.consumeAction(GameStatusChange(player3, Game.Status.Finished))
        eventGate.eventManager.consumeAction(BoardMove(player3, diceValue = 2))
        eventGate.eventManager.consumeAction(GameRoll(player3, Game.Id(4)))
        //
        val playersHistory = eventGate.stateHolder.currentPlayersHistory
        // compare player 1
        assertEquals(expected = 1, playersHistory[player1]!!.turns.size)
        assertEquals(expected = 0..1, playersHistory[player1]!!.turns.first().moveRange)
        assertEquals(expected = Game.Id(1), playersHistory[player1]!!.turns.first().game?.game?.id)
        assertEquals(expected = Game.Status.InProgress, playersHistory[player1]!!.turns.first().game?.status)
        assertEquals(expected = 1.0, playersHistory[player1]!!.statistics.averageDice)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.finishedGames)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.droppedGames)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.rerolledGames)
        // compare player 2
        assertEquals(expected = 1, playersHistory[player2]!!.turns.size)
        assertEquals(expected = 0..4, playersHistory[player2]!!.turns.first().moveRange)
        assertEquals(expected = Game.Id(2), playersHistory[player2]!!.turns.first().game?.game?.id)
        assertEquals(expected = Game.Status.InProgress, playersHistory[player2]!!.turns.first().game?.status)
        assertEquals(expected = 4.0, playersHistory[player2]!!.statistics.averageDice)
        assertEquals(expected = 0, playersHistory[player2]!!.statistics.finishedGames)
        assertEquals(expected = 0, playersHistory[player2]!!.statistics.droppedGames)
        assertEquals(expected = 0, playersHistory[player2]!!.statistics.rerolledGames)
        // compare player 3
        assertEquals(expected = 2, playersHistory[player3]!!.turns.size)
        assertEquals(expected = 0..5, playersHistory[player3]!!.turns.first().moveRange)
        assertEquals(expected = Game.Id(2), playersHistory[player3]!!.turns.first().game?.game?.id)
        assertEquals(expected = Game.Status.Finished, playersHistory[player3]!!.turns.first().game?.status)
        assertEquals(expected = 5..7, playersHistory[player3]!!.turns.last().moveRange)
        assertEquals(expected = Game.Id(4), playersHistory[player3]!!.turns.last().game?.game?.id)
        assertEquals(expected = Game.Status.InProgress, playersHistory[player3]!!.turns.last().game?.status)
        assertEquals(expected = 3.5, playersHistory[player3]!!.statistics.averageDice)
        assertEquals(expected = 1, playersHistory[player3]!!.statistics.finishedGames)
        assertEquals(expected = 0, playersHistory[player3]!!.statistics.droppedGames)
        assertEquals(expected = 0, playersHistory[player3]!!.statistics.rerolledGames)
    }

    @Test
    fun `player turns calculation - 2`() = runTest {
        val eventGate = provideEventGate()
        val (player1) = eventGate.stateHolder.participants.map { it.name }
        eventGate.eventManager.startHandling()
        // player 1
        eventGate.eventManager.consumeAction(BoardMove(player1, diceValue = 4))
        eventGate.eventManager.consumeAction(GameRoll(player1, Game.Id(1)))
        eventGate.eventManager.consumeAction(GameDrop(player1, diceValue = 3))
        eventGate.eventManager.consumeAction(GameRoll(player1, Game.Id(2)))
        //
        val playersHistory = eventGate.stateHolder.currentPlayersHistory
        // compare player 1
        assertEquals(expected = 2, playersHistory[player1]!!.turns.size)
        assertEquals(expected = 0..1, playersHistory[player1]!!.turns.first().moveRange)
        assertEquals(expected = Game.Status.Dropped, playersHistory[player1]!!.turns.first().game?.status)
        assertEquals(expected = null, playersHistory[player1]!!.turns.last().moveRange)
        assertEquals(expected = Game.Status.InProgress, playersHistory[player1]!!.turns.last().game?.status)
        assertEquals(expected = 4.0, playersHistory[player1]!!.statistics.averageDice)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.finishedGames)
        assertEquals(expected = 1, playersHistory[player1]!!.statistics.droppedGames)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.rerolledGames)
    }

    @Test
    fun `player turns calculation - 3`() = runTest {
        val eventGate = provideEventGate()
        val (player1) = eventGate.stateHolder.participants.map { it.name }
        eventGate.eventManager.startHandling()
        // player 1
        eventGate.eventManager.consumeAction(BoardMove(player1, diceValue = 4))
        val dontCare = DontCare.create()
        eventGate.eventManager.consumeAction(ItemReceive(player1, dontCare))
        eventGate.eventManager.consumeAction(GameRoll(player1, Game.Id(1)))
        eventGate.eventManager.consumeAction(GameSet(player1, Game.Id(2)))
        //
        val playersHistory = eventGate.stateHolder.currentPlayersHistory
        // compare player 1
        assertEquals(expected = 2, playersHistory[player1]!!.turns.size)
        assertEquals(expected = 0..4, playersHistory[player1]!!.turns.first().moveRange)
        assertEquals(expected = Game.Status.InProgress, playersHistory[player1]!!.turns.first().game?.status)
        assertEquals(expected = null, playersHistory[player1]!!.turns.last().moveRange)
        assertEquals(expected = Game.Status.Next, playersHistory[player1]!!.turns.last().game?.status)
        assertEquals(expected = 4.0, playersHistory[player1]!!.statistics.averageDice)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.finishedGames)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.droppedGames)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.rerolledGames)
    }

    @Test
    fun `player turns calculation - 4`() = runTest {
        val eventGate = provideEventGate()
        val (player1) = eventGate.stateHolder.participants.map { it.name }
        eventGate.eventManager.startHandling()
        // player 1
        eventGate.eventManager.consumeAction(BoardMove(player1, diceValue = 4))
        val dontCare = DontCare.create()
        eventGate.eventManager.consumeAction(ItemReceive(player1, dontCare))
        eventGate.eventManager.consumeAction(GameSet(player1, Game.Id(2)))
        //
        val playersHistory = eventGate.stateHolder.currentPlayersHistory
        // compare player 1
        assertEquals(expected = 1, playersHistory[player1]!!.turns.size)
        assertEquals(expected = 0..4, playersHistory[player1]!!.turns.first().moveRange)
        assertEquals(expected = Game.Status.InProgress, playersHistory[player1]!!.turns.first().game?.status)
        assertEquals(expected = 4.0, playersHistory[player1]!!.statistics.averageDice)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.finishedGames)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.droppedGames)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.rerolledGames)
    }

    @Test
    fun `player turns calculation - 5`() = runTest {
        val eventGate = provideEventGate()
        val (player1) = eventGate.stateHolder.participants.map { it.name }
        eventGate.eventManager.startHandling()
        // player 1
        eventGate.eventManager.consumeAction(BoardMove(player1, diceValue = 4))
        eventGate.eventManager.consumeAction(GameRoll(player1, Game.Id(1)))
        eventGate.eventManager.consumeAction(GameStatusChange(player1, gameNewStatus = Game.Status.Finished))
        eventGate.eventManager.consumeAction(BoardMove(player1, diceValue = 2))
        //
        val playersHistory = eventGate.stateHolder.currentPlayersHistory
        // compare player 1
        assertEquals(expected = 2, playersHistory[player1]!!.turns.size)
        assertEquals(expected = 0..4, playersHistory[player1]!!.turns.first().moveRange)
        assertEquals(expected = 4..6, playersHistory[player1]!!.turns.last().moveRange)
        assertEquals(expected = Game.Status.Finished, playersHistory[player1]!!.turns.first().game?.status)
        assertEquals(expected = null, playersHistory[player1]!!.turns.last().game?.status)
        assertEquals(expected = 3.0, playersHistory[player1]!!.statistics.averageDice)
        assertEquals(expected = 1, playersHistory[player1]!!.statistics.finishedGames)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.droppedGames)
        assertEquals(expected = 0, playersHistory[player1]!!.statistics.rerolledGames)
    }
}
