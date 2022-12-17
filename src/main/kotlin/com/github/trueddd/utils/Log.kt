package com.github.trueddd.utils

object Log {

    fun info(tag: String, message: String) {
        println("$tag: $message")
    }

    fun error(tag: String, message: String) {
        System.err.println("$tag: $message")
    }
}
