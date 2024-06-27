package com.github.trueddd.data.repository

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GameGenreDistribution
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.RadioCoverage
import com.github.trueddd.data.model.SavedConfig
import com.github.trueddd.data.model.SavedState
import com.github.trueddd.utils.serialization
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
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
) : GameStateRepository {

    companion object {
        const val TAG = "MongoGameStateRepository"
    }

    private val configCollection by lazy {
        mongoDb.getCollection<Document>("config")
    }

    private val actionsCollection by lazy {
        mongoDb.getCollection<Document>("game_state")
    }

    override suspend fun save(globalState: GlobalState, actions: List<Action>) {
        val config = SavedConfig(
            start = globalState.startDate,
            end = globalState.endDate,
            map = globalState.gameGenreDistribution.genres.joinToString("") { it.ordinal.toString() },
            radio = globalState.radioCoverage.coverage.joinToString("") { it.ordinal.toString() },
            points = globalState.stateSnapshot.overallAmountOfPointsRaised,
        )
        configCollection.deleteMany(Filters.empty())
        configCollection.insertOne(Document.parse(serialization.encodeToString(config)))
        val events = actions.map { Document.parse(serialization.encodeToString(it)) }
        actionsCollection.deleteMany(Filters.empty())
        actionsCollection.insertMany(events)
    }

    override suspend fun load(): SavedState {
        val savedConfig = configCollection.find<Document>(Filters.empty()).first()
            .let { serialization.decodeFromString(SavedConfig.serializer(), it.toJson()) }
        val mapLayout = serialization.decodeFromString(
            GameGenreDistribution.serializer(),
            "\"${savedConfig.map}\""
        )
        val radioCoverage = serialization.decodeFromString(
            RadioCoverage.serializer(),
            "\"${savedConfig.radio}\""
        )
        val events = actionsCollection.find<Document>(Filters.empty()).toList()
            .map { serialization.decodeFromString(Action.serializer(), it.toJson()) }
        return SavedState.Success(
            timeRange = savedConfig.start..savedConfig.end,
            mapLayout = mapLayout,
            radioCoverage = radioCoverage,
            actions = events,
            pointsCollected = savedConfig.points,
        )
    }
}
