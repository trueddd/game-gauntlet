package com.github.trueddd.utils

import kotlinx.serialization.json.Json

val serialization = Json {
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
}
