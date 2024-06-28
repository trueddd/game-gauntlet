package com.github.trueddd.data.repository

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GameGenreDistribution
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.RadioCoverage
import com.github.trueddd.data.model.SavedState
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.serialization
import kotlinx.serialization.encodeToString

abstract class BaseGameStateRepository : GameStateRepository {

    private companion object {
        const val TAG = "BaseGameSaveRepository"
    }

    protected abstract suspend fun writeData(data: List<String>)

    protected abstract suspend fun readData(): List<String>

    override suspend fun save(globalState: GlobalState, actions: List<Action>) {
        val timeRange = "${globalState.startDate}:${globalState.endDate}"
        val mapLayout = serialization.encodeToString(
            GameGenreDistribution.serializer(),
            globalState.gameGenreDistribution
        )
        val radioCoverage = serialization.encodeToString(
            RadioCoverage.serializer(),
            globalState.radioCoverage
        )
        val events = actions
            .asReversed()
            .map { serialization.encodeToString(it) }
        val dataToSave = buildList {
            add("time:$timeRange")
            add("map:$mapLayout")
            add("radio:$radioCoverage")
            add("points:${globalState.stateSnapshot.overallAmountOfPointsRaised}")
            addAll(events)
        }
        writeData(dataToSave)
    }

    override suspend fun load(): SavedState {
        val records = readData()
        Log.info(TAG, "Records found: ${records.size}")
        if (records.isEmpty()) {
            return SavedState.NoRecords
        }
        val (start, end) = records.firstOrNull { it.startsWith("time:") }
            ?.removePrefix("time:")
            ?.split(":")
            ?.let { (start, end) -> start.toLong() to end.toLong() }
            ?: return SavedState.TimeRangeParsingError
        val mapLayout = records.firstOrNull { it.startsWith("map:") }
            ?.removePrefix("map:")
            ?.let { serialization.decodeFromString(GameGenreDistribution.serializer(), it) }
            ?: return SavedState.MapLayoutParsingError
        val radioCoverage = records.firstOrNull { it.startsWith("radio:") }
            ?.removePrefix("radio:")
            ?.let { serialization.decodeFromString(RadioCoverage.serializer(), it) }
            ?: RadioCoverage.generateRandom()
        val pointsCollected = records.firstOrNull { it.startsWith("points:") }
            ?.removePrefix("points:")
            ?.toLongOrNull() ?: 0L
        val events = records
            .filter { it.isNotBlank() }
            .mapNotNull {
                try {
                    serialization.decodeFromString(Action.serializer(), it)
                } catch (_: Exception) {
                    null
                }
            }
        return SavedState.Success(
            timeRange = start..end,
            mapLayout = mapLayout,
            radioCoverage = radioCoverage,
            actions = events,
            pointsCollected = pointsCollected,
        )
    }
}
