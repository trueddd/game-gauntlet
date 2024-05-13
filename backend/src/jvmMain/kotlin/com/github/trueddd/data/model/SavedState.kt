package com.github.trueddd.data.model

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GameGenreDistribution
import com.github.trueddd.data.RadioCoverage

sealed class SavedState {
    class Success(
        val timeRange: LongRange,
        val mapLayout: GameGenreDistribution,
        val radioCoverage: RadioCoverage,
        val actions: List<Action>,
        val pointsCollected: Long,
    ) : SavedState()
    data object NoRecords : SavedState()
    data object TimeRangeParsingError : SavedState()
    data object MapLayoutParsingError : SavedState()
}
