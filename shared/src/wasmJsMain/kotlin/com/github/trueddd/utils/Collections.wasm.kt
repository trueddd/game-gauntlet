package com.github.trueddd.utils

import com.github.trueddd.items.WheelItem

actual fun getItemFactoriesSet(): Set<WheelItem.Factory> {
    throw IllegalStateException("This method must not be called from WASM")
}
