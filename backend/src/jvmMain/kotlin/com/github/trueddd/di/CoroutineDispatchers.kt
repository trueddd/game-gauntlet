package com.github.trueddd.di

import kotlinx.coroutines.CoroutineDispatcher

class CoroutineDispatchers(
    val io: CoroutineDispatcher,
    val default: CoroutineDispatcher,
)
