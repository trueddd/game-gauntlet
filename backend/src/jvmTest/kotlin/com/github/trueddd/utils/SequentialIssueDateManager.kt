package com.github.trueddd.utils

import com.github.trueddd.actions.IssueDateManager
import java.util.concurrent.atomic.AtomicLong

internal class SequentialIssueDateManager : IssueDateManager {

    private val generator = AtomicLong(0L)

    override fun getIssueDate(): Long {
        return generator.getAndIncrement()
    }
}
