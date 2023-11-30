package com.github.trueddd.utils

import kotlin.js.Date

actual object Timer {

    actual fun currentTimeMillis(): Long {
        return Date().getTime().toLong()
    }
}
