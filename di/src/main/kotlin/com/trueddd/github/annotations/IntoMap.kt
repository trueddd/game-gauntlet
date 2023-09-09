package com.trueddd.github.annotations

@Suppress("unused")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IntoMap(val mapName: String, val key: Int)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActionHandler(val key: Int) {
    companion object {
        const val TAG = "ActionHandlers"
    }
}
