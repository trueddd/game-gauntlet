package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.items.FewLetters
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FewLettersItemTest : EventGateTest() {

    @Test
    fun `roll game with debuff - fail`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, FewLetters.create()))
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(GameRoll(user, Game.Id(1)))
        assertNull(lastGameOf(user))
        assertTrue(effectsOf(user).isNotEmpty())
    }

    @Test
    fun `roll game with debuff - success`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, FewLetters.create()))
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(GameRoll(user, Game.Id(0)))
        assertNotNull(lastGameOf(user))
        assertTrue(effectsOf(user).isEmpty())
    }
}
