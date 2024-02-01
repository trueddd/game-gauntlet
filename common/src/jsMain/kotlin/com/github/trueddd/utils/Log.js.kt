package com.github.trueddd.utils

actual object Log {

    actual fun info(tag: String, message: String) {
        console.log("$tag: $message")
    }

    actual fun error(tag: String, message: String) {
        console.error("$tag: $message")
    }
}
