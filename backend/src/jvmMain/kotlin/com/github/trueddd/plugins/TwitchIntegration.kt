package com.github.trueddd.plugins

import com.github.trueddd.core.CommunityFundRaisingTracker
import com.github.trueddd.core.HttpClient
import com.github.trueddd.data.model.RewardRedemption
import com.github.trueddd.data.repository.TwitchUsersRepository
import com.github.trueddd.utils.Environment
import com.github.trueddd.utils.Log
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val REDEMPTIONS_PAGE_LIMIT = 50
private const val TAG = "TwitchIntegration"

fun Application.twitchIntegration() {
    val httpClient by inject<HttpClient>()
    val twitchUsersRepository by inject<TwitchUsersRepository>()
    val communityFundRaisingTracker by inject<CommunityFundRaisingTracker>()
    launch(Dispatchers.Default) {
        delay(10.seconds)
        while (isActive && this@twitchIntegration.isActive) {
            Log.info(TAG, "Collecting Twitch rewards redemptions...")
            val users = twitchUsersRepository.getUsers()
            var paginationCursor: String?
            val redemptions = mutableListOf<RewardRedemption>()
            for (user in users) {
                if (user.rewardId == null) {
                    continue
                }
                val token = httpClient.validateTwitchToken(user.twitchToken).getOrNull()
                    ?.takeIf { it.expiresIn > 1.minutes.inWholeSeconds }
                    ?.let { user.twitchToken }
                    ?: continue
                paginationCursor = null
                do {
                    val result = httpClient.fetchRedemptions(
                        broadcasterId = user.id,
                        rewardId = user.rewardId,
                        token = token,
                        after = paginationCursor,
                        pageSize = REDEMPTIONS_PAGE_LIMIT
                    ).getOrNull() ?: break
                    paginationCursor = result.pagination?.cursor
                    redemptions.addAll(result.data)
                } while (result.data.size > REDEMPTIONS_PAGE_LIMIT)
            }
            val collectedPoints = redemptions
                .sumOf { it.reward.cost.toLong() }
            Log.info(TAG, "Collected $collectedPoints points")
            redemptions.clear() // Clear the unused memory, list might very large
            communityFundRaisingTracker.updateOverallAmountRaised(collectedPoints)
            delay(if (Environment.IsDev) 2.minutes else 30.minutes)
        }
    }
}
