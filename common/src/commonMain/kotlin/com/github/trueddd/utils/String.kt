package com.github.trueddd.utils

fun String.removeTabs(indentChar: Char = '|'): String {
    return lines()
        .filter { it.isNotBlank() }
        .joinToString("") { it.substring(it.indexOf(indentChar) + 1, it.length) }
}
