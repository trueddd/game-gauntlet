package com.github.trueddd.theme

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
object Colors {
    val Background = Color(0xFF0F172A)
    val DarkBackground = Color(0xFF020617)
    val SecondaryBackground = Color(0xFF1E293B)
    val Text = Color(0xFFF3F4F6)
    val TextSecondary = Color(0xFF9CA3AF)
    val Primary = Color(0xFF1D4ED8)
    val Warning = Color(0xFFFBBF24)
    val Error = Color(0xFFF87171)
    val Success = Color(0xFF4ADE80)
    object Genre {
        val Runner = Color(0xFFF87171)
        val Business = Color(0xFF60A5FA)
        val Puzzle = Color(0xFFA78BFA)
        val PointAndClick = Color(0xFF4ADE80)
        val Shooter = Color(0xFFFBBF24)
        val ThreeInRow = Color(0xFFFB923C)
        val Special = Color(0xFF9CA3AF)
        val Default = Color(0xFFECFDF5)
    }
    object WheelItem {
        val Event = Color(0xFF06B6D4)
        val PendingEvent = Color(0xFF14B8A6)
        val InventoryItem = Color(0xFFF59E0B)
        val Buff = Color(0xFF22C55E)
        val Debuff = Color(0xFFEF4444)
    }
}
