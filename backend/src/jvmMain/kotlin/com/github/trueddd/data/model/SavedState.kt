package com.github.trueddd.data.model

import com.github.trueddd.actions.Action
import com.github.trueddd.data.model.save.GameConfig

sealed class SavedState {
    class Success(
        val gameConfig: GameConfig,
        val actions: List<Action>,
    ) : SavedState()
    data object NoRecords : SavedState()
    data object TimeRangeParsingError : SavedState()
    data object MapLayoutParsingError : SavedState()
}
