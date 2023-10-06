package com.github.trueddd.items

actual fun generateWheelItemUid(): String {
    return js("crypto.randomUUID()") as String
}
