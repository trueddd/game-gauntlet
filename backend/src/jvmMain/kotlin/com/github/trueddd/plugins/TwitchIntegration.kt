package com.github.trueddd.plugins

import com.github.trueddd.actions.GlobalEvent
import com.github.trueddd.core.CommunityFundRaisingTracker
import com.github.trueddd.core.EventGate
import com.github.trueddd.core.HttpClient
import com.github.trueddd.data.ScheduledEvent
import com.github.trueddd.data.model.RewardRedemption
import com.github.trueddd.data.model.SavedTwitchUserData
import com.github.trueddd.data.repository.TwitchUsersRepository
import com.github.trueddd.di.CoroutineDispatchers
import com.github.trueddd.utils.Environment
import com.github.trueddd.utils.Log
import io.ktor.server.application.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val REDEMPTIONS_PAGE_LIMIT = 50
private const val TAG = "TwitchIntegration"

fun Application.twitchIntegration() {
    val httpClient by inject<HttpClient>()
    val twitchUsersRepository by inject<TwitchUsersRepository>()
    val communityFundRaisingTracker by inject<CommunityFundRaisingTracker>()
    val eventGate by inject<EventGate>()
    val dispatchers by inject<CoroutineDispatchers>()

    launch(dispatchers.default) {
        eventGate.stateHolder.currentStageFlow
            .distinctUntilChanged()
            .filter { stage ->
                val numberOfEvents = eventGate.historyHolder.getActions().count { it is GlobalEvent }
                numberOfEvents < stage && eventGate.stateHolder.current.stateSnapshot.scheduledEvent == null
            }
            .collectLatest {
                val delayTime = if (Environment.IsDev) 1.minutes else 60.minutes
                eventGate.stateHolder.update {
                    copy(stateSnapshot = stateSnapshot.copy(scheduledEvent = ScheduledEvent(
                        eventType = GlobalEvent.Type.entries.random(),
                        startTime = (Clock.System.now() + delayTime).toEpochMilliseconds(),
                        epicenterStintIndex = eventGate.stateHolder.current.getMostPopulatedStintIndex(),
                    )))
                }
                Log.info(TAG, "Scheduled new event: ${eventGate.stateHolder.current.stateSnapshot.scheduledEvent}")
                delay(delayTime)
                val scheduledEvent = eventGate.stateHolder.current.stateSnapshot.scheduledEvent ?: return@collectLatest
                val action = GlobalEvent(
                    type = scheduledEvent.eventType,
                    stageIndex = it - 1,
                    epicenterStintIndex = scheduledEvent.epicenterStintIndex,
                )
                val handledAction = eventGate.eventManager.consumeAction(action)
                if (handledAction.error == null) {
                    eventGate.historyHolder.pushEvent(action)
                }
            }
    }
    launch(dispatchers.default) {
        delay(10.seconds)
        while (isActive && this@twitchIntegration.isActive) {
            Log.info(TAG, "Collecting Twitch rewards redemptions...")
            val users = twitchUsersRepository.getUsersFlow().toList()
            val redemptions = users.flatMap { httpClient.getRedemptionsByUser(it) }
            val collectedPoints = redemptions.sumOf { it.reward.cost.toLong() }
            Log.info(TAG, "Collected $collectedPoints points")
            communityFundRaisingTracker.updateOverallAmountRaised(collectedPoints)
            delay(if (Environment.IsDev) 1.minutes else 30.minutes)
        }
    }
}

private suspend fun HttpClient.getRedemptionsByUser(
    user: SavedTwitchUserData,
): List<RewardRedemption> {
    if (user.rewardId == null) {
        return emptyList()
    }
    val token = validateTwitchToken(user.twitchToken).getOrNull()
        ?.takeIf { it.expiresIn > 1.minutes.inWholeSeconds }
        ?.let { user.twitchToken }
        ?: return emptyList()
    var paginationCursor: String? = null
    val redemptions = mutableListOf<RewardRedemption>()
    do {
        val result = fetchRedemptions(
            broadcasterId = user.id,
            rewardId = user.rewardId,
            token = token,
            after = paginationCursor,
            pageSize = REDEMPTIONS_PAGE_LIMIT
        ).getOrNull() ?: break
        paginationCursor = result.pagination?.cursor
        redemptions.addAll(result.data)
    } while (result.data.size > REDEMPTIONS_PAGE_LIMIT)
    return redemptions
}
