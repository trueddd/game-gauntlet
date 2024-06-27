package com.github.trueddd.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MultilineStringFormattingTest {

    @Test
    fun removeTabsProperlyFromTwoLines() {
        val string = """
            |This is the first line. 
            |That one is the second.
        """
        val expected = "This is the first line. That one is the second."
        assertEquals(expected, string.removeTabs())
    }

    @Test
    fun removeTabsProperlyFromThreeLines() {
        val string = """
            |This is the first line. 
            |That one is the second. 
            |And this is the last one.
        """
        val expected = "This is the first line. That one is the second. And this is the last one."
        assertEquals(expected, string.removeTabs())
    }

    @Test
    fun tabsRemovalFail() {
        val string = """
            |This is the first line. 
            |That one is the second.
        """
        val illegal = "This is the first line. That one is the second."
        assertNotEquals(illegal, string.removeTabs('!'))
    }
}
