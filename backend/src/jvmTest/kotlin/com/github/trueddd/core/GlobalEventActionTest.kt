package com.github.trueddd.core

import com.github.trueddd.EventGateTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GlobalEventActionTest : EventGateTest() {

    @Test
    fun `sample test`() = runTest {
        assertTrue(false)
    }
}
