package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ImportSubstitutionItemTest : EventGateTest() {

    @Test
    fun `debuff active till the end`() = runTest {
        val user = requireRandomParticipant()
        handleAction(ItemReceive(user, ImportSubstitution.create()))
        makeMovesUntilFinish(user)
        assertEquals(user, eventGate.stateHolder.current.winner)
        assertIs<ImportSubstitution>(effectsOf(user).first())
    }
}
