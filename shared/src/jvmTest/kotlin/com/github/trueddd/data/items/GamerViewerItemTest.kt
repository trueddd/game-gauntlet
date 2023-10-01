package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.actions.GameRoll
import com.github.trueddd.actions.GameStatusChange
import com.github.trueddd.actions.ItemReceive
import com.github.trueddd.data.Game
import com.github.trueddd.items.Gamer
import com.github.trueddd.items.Viewer
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GamerViewerItemTest : EventGateTest() {

    @Test
    fun `gamer item test 1`() = runTest {
        val user = requireRandomParticipant()
        val item = Gamer.create()
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemReceive(user, item))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = item.maxCharges, effectsOf(user).filterIsInstance<Gamer>().first().chargesLeft)
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(GameRoll(user, Game.Id(3)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertTrue(effectsOf(user).none { it is Gamer })
    }

    @Test
    fun `gamer item test 2`() = runTest {
        val user = requireRandomParticipant()
        val item = Gamer.create()
        handleAction(ItemReceive(user, item))
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertTrue(effectsOf(user).none { it is Gamer })
    }

    @Test
    fun `viewer item test`() = runTest {
        val user = requireRandomParticipant()
        val item = Viewer.create()
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemReceive(user, item))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertEquals(expected = item.maxCharges, effectsOf(user).filterIsInstance<Viewer>().first().chargesLeft)
        handleAction(GameRoll(user, Game.Id(2)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(GameRoll(user, Game.Id(3)))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        assertTrue(effectsOf(user).none { it is Viewer })
    }

    @Test
    fun `game-viewer item test`() = runTest {
        val user = requireRandomParticipant()
        val gamerItem = Gamer.create()
        val viewerItem = Viewer.create()
        handleAction(GameRoll(user, Game.Id(1)))
        handleAction(ItemReceive(user, gamerItem))
        handleAction(GameStatusChange(user, Game.Status.Finished))
        handleAction(ItemReceive(user, viewerItem))
        assertTrue(effectsOf(user).isEmpty())
    }
}
