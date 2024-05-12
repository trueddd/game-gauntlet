package com.github.trueddd.data.repository

import com.github.trueddd.data.PlayersTable
import com.github.trueddd.data.model.SavedTwitchUserData
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single
class TwitchUsersRepository(
    private val database: Database,
) {

    suspend fun updateReward(playerName: String, rewardId: String) {
        suspendedTransactionAsync(Dispatchers.IO, database) {
            if (PlayersTable.select(PlayersTable.name eq playerName).empty()) {
                return@suspendedTransactionAsync
            }
            PlayersTable.update({ PlayersTable.name eq playerName }) {
                it[PlayersTable.rewardId] = rewardId
            }
        }.await()
    }

    suspend fun saveUser(userId: String, userName: String, twitchToken: String) {
        suspendedTransactionAsync(Dispatchers.IO, database) {
            if (PlayersTable.select(PlayersTable.id eq userId).empty()) {
                PlayersTable.insert {
                    it[id] = userId
                    it[name] = userName
                    it[PlayersTable.twitchToken] = twitchToken
                    it[rewardId] = null
                }
            } else {
                PlayersTable.update({ PlayersTable.id eq userId }) {
                    it[PlayersTable.twitchToken] = twitchToken
                }
            }
        }.await()
    }

    suspend fun getUsers(): List<SavedTwitchUserData> {
        return suspendedTransactionAsync(Dispatchers.IO, database) {
            PlayersTable.selectAll().map {
                SavedTwitchUserData(
                    id = it[PlayersTable.id],
                    playerName = it[PlayersTable.name],
                    twitchToken = it[PlayersTable.twitchToken],
                    rewardId = it[PlayersTable.rewardId],
                )
            }
        }.await()
    }
}
