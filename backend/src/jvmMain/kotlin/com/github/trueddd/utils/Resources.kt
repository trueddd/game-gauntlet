package com.github.trueddd.utils

import java.io.InputStream

fun getResourceFile(name: String): InputStream? {
    return Thread.currentThread().contextClassLoader.getResourceAsStream(name)
}
