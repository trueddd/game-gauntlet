package com.github.trueddd.util

import kotlinx.browser.window

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
