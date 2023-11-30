package com.github.trueddd.utils

import com.github.trueddd.items.WheelItem

actual fun getItemFactoriesSet(): Set<WheelItem.Factory> {
    return com.github.trueddd.di.getItemFactoriesSet()
}
