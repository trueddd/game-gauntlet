package com.trueddd.github.annotations

@Suppress("unused")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IntoSet(val setName: String)
