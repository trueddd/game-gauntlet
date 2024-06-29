package com.github.trueddd.di

import com.github.trueddd.actions.IssueDateManager
import kotlinx.datetime.Clock

class TimedIssueDateManager : IssueDateManager {

    override fun getIssueDate(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}
