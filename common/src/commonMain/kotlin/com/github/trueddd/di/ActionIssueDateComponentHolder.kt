package com.github.trueddd.di

import com.github.trueddd.actions.IssueDateManager
import kotlin.concurrent.Volatile

object ActionIssueDateComponentHolder : ComponentHolder<IssueDateManager> {

    @Volatile
    private var component: IssueDateManager? = null

    override fun set(component: IssueDateManager) {
        this.component = component
    }

    override fun get(): IssueDateManager {
        return requireNotNull(component)
    }
}
