package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.BoardMove
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.actions.ItemUse
import com.github.trueddd.items.Relocation
import com.github.trueddd.map.Genre
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RelocationItemTest : EventGateTest() {

    @Test
    fun `relocate backward`() = runTest {
        val user = getRandomPlayerName()
        val item = Relocation.create()
        handleAction(BoardMove(user, diceValue = 6))
        handleAction(ItemReceive(user, item))
        val genreBack = genreAtPosition(5)
        handleAction(ItemUse(user, item.uid, listOf(genreBack.ordinal.toString())))
        assertEquals(expected = 5, positionOf(user))
    }

    @Test
    fun `relocate forward`() = runTest {
        val user = getRandomPlayerName()
        val item = Relocation.create()
        handleAction(BoardMove(user, diceValue = 5))
        handleAction(ItemReceive(user, item))
        val genreBack = genreAtPosition(6)
        handleAction(ItemUse(user, item.uid, listOf(genreBack.ordinal.toString())))
        assertEquals(expected = 6, positionOf(user))
    }

    @RepeatedTest(20)
    fun `relocate forward when both directions are possible`() = runTest {
        val superposition = eventGate.stateHolder.current.genres.let { genres ->
            genres.forEachIndexed { index, genre ->
                if (genre != Genre.Special) {
                    return@forEachIndexed
                }
                if (genres.getOrNull(index - 2) == genres.getOrNull(index + 2)
                    && genres.getOrNull(index + 2) != null) {
                    return@let index + 1
                }
            }
            return@runTest
        }
        val user = getRandomPlayerName()
        val item = Relocation.create()
        eventGate.stateHolder.update {
            updatePlayer(user) { it.copy(position = superposition) }
        }
        assertEquals(
            genreAtPosition(positionOf(user) - 2),
            genreAtPosition(positionOf(user) + 2)
        )
        handleAction(ItemReceive(user, item))
        val genre = genreAtPosition(positionOf(user) - 2)
        handleAction(ItemUse(user, item.uid, listOf(genre.ordinal.toString())))
        assertEquals(expected = superposition + 2, positionOf(user))
    }
}
