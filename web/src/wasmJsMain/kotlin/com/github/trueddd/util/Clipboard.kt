package com.github.trueddd.util

import kotlinx.browser.window
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun copyToClipBoard(content: String) {
    window.navigator.clipboard.writeText(content)
        .then {
            println("Copied `$content` to clipboard")
            it
        }
        .catch {
            println("Copy error: $it")
            it
        }
}

suspend fun readFromClipboard(): String {
    return suspendCoroutine { continuation ->
        window.navigator.clipboard
            .readText()
            .then {
                continuation.resume(it.toString())
                it
            }
            .catch {
                continuation.resumeWithException(Exception(it.toString()))
                println("Clipboard read error: $it")
                it
            }
    }
}
