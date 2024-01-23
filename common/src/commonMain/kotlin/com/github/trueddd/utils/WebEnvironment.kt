package com.github.trueddd.utils

// TODO: depend on actual environment
object WebEnvironment {
    const val ServerHost = "0.0.0.0"
    const val ServerPort = "8102"
    const val ServerAddress = "$ServerHost:$ServerPort"
    const val ClientHost = "0.0.0.0"
    const val ClientPort = "8080"
    const val ClientAddress = "$ClientHost:$ClientPort"
}
