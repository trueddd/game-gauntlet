package com.github.trueddd.data.items

context (WheelItem)
@Throws(IllegalArgumentException::class)
fun List<String>.getBooleanParameter(index: Int = 0): Boolean {
    return when (val value = this.getOrNull(index)) {
        "1" -> true
        "0" -> false
        else -> throw IllegalArgumentException("Boolean argument must be passed, but was $value")
    }
}
