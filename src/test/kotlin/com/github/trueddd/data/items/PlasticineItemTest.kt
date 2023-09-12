package com.github.trueddd.data.items

import com.github.trueddd.EventGateTest
import com.github.trueddd.core.actions.ItemReceive
import com.github.trueddd.core.actions.ItemUse
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertIs

class PlasticineItemTest : EventGateTest() {

    @Test
    fun `transform into a rope`() = runTest {
        val user = requireParticipant("shizov")
        val item = Plasticine.create()
        handleAction(ItemReceive(user, item))
        assertIs<Plasticine>(inventoryOf(user).first())
        handleAction(ItemUse(user, item.uid, listOf(WheelItem.Id.ClimbingRope.asString())))
        assertIs<ClimbingRope>(inventoryOf(user).first())
    }
}
