package com.github.trueddd.utils

actual object Log {

    actual fun info(tag: String, message: String) {
        println("$tag: $message")
    }

    actual fun error(tag: String, message: String) {
        System.err.println("$tag: $message")
    }
}
