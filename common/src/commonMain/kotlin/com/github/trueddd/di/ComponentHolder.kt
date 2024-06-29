package com.github.trueddd.di

interface ComponentHolder<T> {

    fun set(component: T)

    @Throws(IllegalStateException::class)
    fun get(): T
}
