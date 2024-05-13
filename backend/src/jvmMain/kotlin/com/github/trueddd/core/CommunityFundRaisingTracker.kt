package com.github.trueddd.core

import kotlinx.coroutines.flow.Flow

interface CommunityFundRaisingTracker {

    val overallAmountRaised: Long

    val raisedAmountOnCurrentStage: Long

    val currentStageFlow: Flow<Int>

    fun updateOverallAmountRaised(amount: Long)
}
