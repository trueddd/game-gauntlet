package com.github.trueddd.utils

actual object Timer {

    actual fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}
