package com.github.trueddd

import dev.fritz2.core.render

fun main() {
    render("#target") {
        h1 { + "AGG2 page" }
        div("css") {
            + "Text"
        }
    }
}
