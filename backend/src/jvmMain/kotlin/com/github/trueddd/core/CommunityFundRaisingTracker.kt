package com.github.trueddd.core

interface CommunityFundRaisingTracker {

    companion object {
        const val SINGLE_EVENT_CAP = 10_000L
    }

    val overallAmountRaised: Long

    val raisedAmountOnCurrentStage: Long

    val currentStage: Int

    fun updateOverallAmountRaised(amount: Long)
}
