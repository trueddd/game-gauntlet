package com.trueddd.github.annotations

@Suppress("unused")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IntoSet(val setName: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActionGenerator {
    companion object {
        const val TAG = "ActionGenerators"
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ItemFactory {
    companion object {
        const val TAG = "ItemFactories"
    }
}
