package com.github.trueddd.ui.profile

import androidx.compose.runtime.Immutable
import com.github.trueddd.items.WheelItem

@Immutable
sealed class ProfileDialogs {
    data object None : ProfileDialogs()
    data object GameStatusChange : ProfileDialogs()
    data object BoardMove : ProfileDialogs()
    data class WheelItemView(val item: WheelItem) : ProfileDialogs()
}
