package com.trueddd.github.annotations

@Suppress("unused")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IntoMap(val mapName: String, val key: Int)
