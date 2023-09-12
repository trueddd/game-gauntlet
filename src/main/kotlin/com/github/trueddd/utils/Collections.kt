package com.github.trueddd.utils

fun <T> Collection<T>.powerSet(): List<List<T>> = powerSet(this, listOf(listOf()))

private tailrec fun <T> powerSet(left: Collection<T>, acc: List<List<T>>): List<List<T>> = when {
    left.isEmpty() -> acc
    else -> powerSet(left.drop(1), acc + acc.map { it + left.first() })
}
