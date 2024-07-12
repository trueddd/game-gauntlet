package com.github.trueddd.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.trueddd.data.GameConfig
import com.github.trueddd.data.StateSnapshot
import com.github.trueddd.utils.GlobalEventConstants

// TODO: Put sectors according to positions
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Map(
    gameConfig: GameConfig,
    stateSnapshot: StateSnapshot?,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(32.dp)
            .fillMaxWidth()
    ) {
        val mapState = remember(stateSnapshot, gameConfig) {
            gameConfig.mapConfig.sectors.map { sector ->
                MapSectorState(
                    index = sector.index,
                    genre = sector.genre,
                    players = stateSnapshot?.playersState
                        ?.filterValues { it.position == sector.index }?.keys
                        ?.let { names -> gameConfig.players.filter { it.name in names } }
                        ?: emptyList(),
                    traps = stateSnapshot?.boardTraps?.filterKeys { it == sector.index }
                        ?.values?.toList()
                        ?: emptyList(),
                    radioStation = sector.radio,
                )
            }
        }
        if (stateSnapshot != null) {
            if (stateSnapshot.scheduledEvent != null) {
                GlobalEventAnnouncement(
                    scheduledEvent = stateSnapshot.scheduledEvent!!,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else {
                GlobalEventCounter(
                    pointsAmount = stateSnapshot.overallAmountOfPointsRaised % GlobalEventConstants.EVENT_CAP,
                    pointsCap = GlobalEventConstants.EVENT_CAP,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            mapState.forEach {
                Sector(it)
            }
        }
    }
}
