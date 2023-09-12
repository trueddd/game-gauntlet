package com.trueddd.github.annotations

@Suppress("unused")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IntoSet(val setName: String)

/**
 * Marks an action generator class, so it will be added to the generated list of action generators.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActionGenerator {
    companion object {
        const val TAG = "ActionGenerators"
    }
}

/**
 * Marks a factory class for wheel item, so marked factory will be added to the generated list of factories.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ItemFactory {
    companion object {
        const val TAG = "ItemFactories"
    }
}
