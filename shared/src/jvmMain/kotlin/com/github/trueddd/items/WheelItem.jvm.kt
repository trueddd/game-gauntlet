package com.github.trueddd.items

import java.util.*

actual fun generateWheelItemUid(): String {
    return UUID.randomUUID().toString()
}
