package com.github.trueddd.data.repository

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.model.SavedState
import com.github.trueddd.data.model.save.GameConfig
import com.github.trueddd.di.CoroutineDispatchers
import com.github.trueddd.map.MapConfig
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.serialization
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.encodeToString
import org.bson.Document
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Named(MongoGameStateRepository.TAG)
@Single
class MongoGameStateRepository(
    private val mongoDb: MongoDatabase,
    private val dispatchers: CoroutineDispatchers,
) : GameStateRepository {

    companion object {
        const val TAG = "MongoGameStateRepository"
    }

    private val mainConfigCollection by lazy {
        mongoDb.getCollection<Document>("main_config")
    }

    private val mapConfigCollection by lazy {
        mongoDb.getCollection<Document>("map_config")
    }

    private val actionsCollection by lazy {
        mongoDb.getCollection<Document>("game_state")
    }

    override suspend fun save(globalState: GlobalState, actions: List<Action>) {
        val gameConfig = GameConfig(
            startTime = globalState.startDate,
            endTime = globalState.endDate,
            pointsCollected = globalState.stateSnapshot.overallAmountOfPointsRaised,
        )
        val mapConfig = globalState.mapConfig

        coroutineScope {
            val mainConfigSaveJob = async(dispatchers.io) {
                mainConfigCollection.deleteMany(Filters.empty())
                mainConfigCollection.insertOne(Document.parse(serialization.encodeToString(gameConfig)))
            }
            val mapConfigSaveJob = async(dispatchers.io) {
                mapConfigCollection.deleteMany(Filters.empty())
                mapConfigCollection.insertOne(Document.parse(serialization.encodeToString(mapConfig)))
            }
            val actionsSaveJob = async(dispatchers.io) {
                val serializedActions = actions.map { Document.parse(serialization.encodeToString(it)) }
                actionsCollection.deleteMany(Filters.empty())
                actionsCollection.insertMany(serializedActions)
            }
            awaitAll(mainConfigSaveJob, mapConfigSaveJob, actionsSaveJob)
        }
    }

    override suspend fun load(): SavedState {
        return coroutineScope {
            val mainConfigDeferred = async(dispatchers.io) {
                val document = mainConfigCollection.find<Document>(Filters.empty()).first()
                serialization.decodeFromString(GameConfig.serializer(), document.toJson())
            }
            val mapConfigDeferred = async(dispatchers.io) {
                val document = mapConfigCollection.find<Document>(Filters.empty()).first()
                serialization.decodeFromString(MapConfig.serializer(), document.toJson())
            }
            val actionsDeferred = async(dispatchers.io) {
                actionsCollection.find<Document>(Filters.empty())
                    .toList()
                    .map { serialization.decodeFromString(Action.serializer(), it.toJson()) }
            }
            val gameConfig = mainConfigDeferred.await()
            Log.info(TAG, "Loaded game config: $gameConfig")
            val mapConfig = mapConfigDeferred.await()
            Log.info(TAG, "Loaded map config, size: ${mapConfig.sectors.size}")
            val actions = actionsDeferred.await()
            Log.info(TAG, "Loaded actions config, size: ${actionsDeferred.await().size}")
            SavedState.Success(
                gameConfig = gameConfig,
                mapConfig = mapConfig,
                actions = actions,
            )
        }
    }
}
