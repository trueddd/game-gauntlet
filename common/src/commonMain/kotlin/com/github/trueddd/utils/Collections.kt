package com.github.trueddd.utils

import com.github.trueddd.items.WheelItem

fun <T> Collection<T>.powerSet(): List<List<T>> = powerSet(this, listOf(listOf()))

private tailrec fun <T> powerSet(left: Collection<T>, acc: List<List<T>>): List<List<T>> = when {
    left.isEmpty() -> acc
    else -> powerSet(left.drop(1), acc + acc.map { it + left.first() })
}

expect fun getItemFactoriesSet(): Set<WheelItem.Factory>

val wheelItems: List<WheelItem> by lazy {
    getItemFactoriesSet().map { it.create() }
}
