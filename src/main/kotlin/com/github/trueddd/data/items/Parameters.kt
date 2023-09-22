package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant

context (WheelItem)
@Throws(IllegalArgumentException::class)
fun List<String>.getBooleanParameter(index: Int = 0): Boolean {
    return when (val value = this.getOrNull(index)) {
        "1" -> true
        "0" -> false
        else -> throw IllegalArgumentException("Boolean argument must be passed, but was $value")
    }
}

context (WheelItem)
@Throws(IllegalArgumentException::class)
fun List<String>.getStringParameter(index: Int = 0): String {
    return this.getOrNull(index)
        ?: throw IllegalArgumentException("String argument must be passed, but was ${getOrNull(index)}")
}

context (WheelItem)
@Throws(IllegalArgumentException::class)
fun List<String>.getIntParameter(index: Int = 0): Int {
    val value = this.getOrNull(index)
        ?: throw IllegalArgumentException("Number must be specified as parameter with index $index")
    return value.toIntOrNull()
        ?: throw IllegalArgumentException("Cannot parse $value as number")
}

context (WheelItem)
@Throws(IllegalArgumentException::class)
fun List<String>.getParticipantParameter(index: Int = 0, globalState: GlobalState): Participant {
    val name = this.getOrNull(index)
        ?: throw IllegalArgumentException("Player name must be specified as parameter with index $index")
    return globalState.participantByName(name)
        ?: throw IllegalArgumentException("No players were found with name $name")
}
