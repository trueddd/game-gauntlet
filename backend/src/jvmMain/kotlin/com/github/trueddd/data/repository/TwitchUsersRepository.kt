package com.github.trueddd.data.repository

import com.github.trueddd.data.model.SavedTwitchUserData
import com.github.trueddd.utils.serialization
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.koin.core.annotation.Single

@Single
class TwitchUsersRepository(
    private val mongoDatabase: MongoDatabase,
) {

    private val userCollection by lazy {
        mongoDatabase.getCollection<Document>("users")
    }

    suspend fun updateReward(playerName: String, rewardId: String) {
        val ref = Updates.set(SavedTwitchUserData::playerName.name, rewardId)
        userCollection.updateOne(Filters.eq(SavedTwitchUserData::playerName.name, playerName), ref)
    }

    suspend fun saveUser(userId: String, userName: String, twitchToken: String) {
        val document = SavedTwitchUserData(
            id = userId,
            playerName = userName,
            twitchToken = twitchToken,
            rewardId = null,
        )
            .let { serialization.encodeToString(SavedTwitchUserData.serializer(), it) }
            .let { Document.parse(it) }
        userCollection.updateOne(
            Filters.eq(SavedTwitchUserData::id.name, userId),
            document,
            UpdateOptions().upsert(true)
        )
    }

    suspend fun getUsers(): List<SavedTwitchUserData> {
        return userCollection.find(Filters.empty())
            .map { serialization.decodeFromString(SavedTwitchUserData.serializer(), it.toJson()) }
            .toList()
    }
}
