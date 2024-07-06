package com.github.trueddd.data.repository

import com.github.trueddd.actions.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.model.SavedState
import com.github.trueddd.data.model.save.ActionsConfig
import com.github.trueddd.data.model.save.GameConfig
import com.github.trueddd.data.model.save.GameSaveFileStructure
import com.github.trueddd.di.CoroutineDispatchers
import com.github.trueddd.map.MapConfig
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.serialization
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.encodeToString
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Named(FileGameStateRepository.TAG)
@Single
class FileGameStateRepository(
    private val gameSaveFileStructure: GameSaveFileStructure,
    private val dispatchers: CoroutineDispatchers,
) : GameStateRepository {

    companion object {
        const val TAG = "FileGameStateRepository"
    }

    init {
        gameSaveFileStructure.createFiles()
    }

    override suspend fun save(globalState: GlobalState, actions: List<Action>) {
        val gameConfig = GameConfig(
            startTime = globalState.startDate,
            endTime = globalState.endDate,
            pointsCollected = globalState.stateSnapshot.overallAmountOfPointsRaised,
        )
        val mapConfig = globalState.mapConfig
        val actionsConfig = ActionsConfig(actions)

        coroutineScope {
            val gameConfigSaveJob = async(dispatchers.io) {
                gameSaveFileStructure.configFile.writeText(serialization.encodeToString(gameConfig))
            }
            val mapConfigSaveJob = async(dispatchers.io) {
                gameSaveFileStructure.mapConfig.writeText(serialization.encodeToString(mapConfig))
            }
            val actionsSaveJob = async(dispatchers.io) {
                gameSaveFileStructure.actionsFile.writeText(serialization.encodeToString(actionsConfig))
            }
            awaitAll(gameConfigSaveJob, mapConfigSaveJob, actionsSaveJob)
        }
    }

    override suspend fun load(): SavedState {
        return coroutineScope {
            val gameConfigDeferred = async(dispatchers.io) {
                val rawString = gameSaveFileStructure.configFile.readText()
                serialization.decodeFromString(GameConfig.serializer(), rawString)
            }
            val mapConfigDeferred = async(dispatchers.io) {
                val rawString = gameSaveFileStructure.mapConfig.readText()
                serialization.decodeFromString(MapConfig.serializer(), rawString)
            }
            val actionsDeferred = async(dispatchers.io) {
                val rawString = gameSaveFileStructure.actionsFile.readText()
                serialization.decodeFromString(ActionsConfig.serializer(), rawString)
            }
            val gameConfig = gameConfigDeferred.await()
            Log.info(TAG, "Loaded game config: $gameConfig")
            val mapConfig = mapConfigDeferred.await()
            Log.info(TAG, "Loaded map config, size ${mapConfig.sectors.size}")
            val actionsConfig = actionsDeferred.await()
            Log.info(TAG, "Loaded actions config, size: ${actionsConfig.actions.size}")
            SavedState.Success(
                gameConfig = gameConfig,
                mapConfig = mapConfig,
                actions = actionsConfig.actions,
            )
        }
    }
}
