package com.github.trueddd.utils

expect object Log {

    fun info(tag: String, message: String)

    fun error(tag: String, message: String)
}
