package com.github.trueddd.data.repository

import com.github.trueddd.data.model.SavedTwitchUserData
import com.github.trueddd.di.CoroutineDispatchers
import com.github.trueddd.utils.serialization
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.bson.Document
import org.koin.core.annotation.Single

@Single
class TwitchUsersRepository(
    private val mongoDatabase: MongoDatabase,
    private val dispatchers: CoroutineDispatchers,
) {

    private val userCollection by lazy {
        mongoDatabase.getCollection<Document>("users")
    }

    suspend fun updateReward(playerName: String, rewardId: String) {
        val ref = Updates.set(SavedTwitchUserData::playerName.name, rewardId)
        userCollection.updateOne(Filters.eq(SavedTwitchUserData::playerName.name, playerName), ref)
    }

    suspend fun saveUser(userId: String, userName: String, twitchToken: String) {
        userCollection.updateOne(
            Filters.eq(SavedTwitchUserData::id.name, userId),
            Updates.combine(
                Updates.set(SavedTwitchUserData::playerName.name, userName),
                Updates.set(SavedTwitchUserData::twitchToken.name, twitchToken),
                Updates.setOnInsert(SavedTwitchUserData::rewardId.name, null),
                Updates.setOnInsert(SavedTwitchUserData::id.name, userId),
            ),
            UpdateOptions().upsert(true)
        )
    }

    fun getUsersFlow(): Flow<SavedTwitchUserData> {
        return userCollection.find(Filters.empty())
            .map { serialization.decodeFromString(SavedTwitchUserData.serializer(), it.toJson()) }
            .flowOn(dispatchers.io)
    }
}
