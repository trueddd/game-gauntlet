package com.github.trueddd.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerState
import com.github.trueddd.items.LostFoot
import com.github.trueddd.items.LostLeg
import com.trueddd.github.annotations.ActionHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.min

@Serializable
@SerialName("a${Action.Key.GlobalEvent}")
data class GlobalEvent(
    @SerialName("ty")
    val type: Type,
    @SerialName("st")
    val stageIndex: Int,
    @SerialName("es")
    val epicenterStintIndex: Int,
) : Action(Key.GlobalEvent) {

    @Serializable
    enum class Type {
        /**
         * Tornado swaps positions of players in the target segment relative to the epicenter.
         * For example, if player is on the 9th sector and epicenter is 10-11 sectors, then
         * his final position will be 12th sector. Players from neighboring segments will be
         * pulled to the epicenter.
         */
        @SerialName("tornado")
        Tornado,

        /**
         * Bomb is exploded in the middle of segment between two special sectors. For example,
         * if two special sectors are 7 and 14, and epicenter is between 10 and 11 sectors.
         */
        @SerialName("nuke")
        Nuke,
    }

    private companion object {
        object Nuke {
            const val LIGHT_DAMAGE_SHIFT = 3
            const val HEAVY_DAMAGE_SHIFT = 6
        }
        object Tornado {
            const val LIGHT_DAMAGE_SHIFT = 3
        }
    }

    private enum class EpicenterDirection {
        Back, Front
    }

    private enum class DamageType {
        None, Light, Heavy
    }

    private data class PlayerEventInfo(
        val damageType: DamageType,
        val epicenterDirection: EpicenterDirection,
        val epicenterRange: IntRange,
    ) {
        val isEpicenterForward: Boolean
            get() = epicenterDirection == EpicenterDirection.Front
    }

    @ActionHandler(key = Key.GlobalEvent)
    class Handler : Action.Handler<GlobalEvent> {

        private fun PlayerState.playerEventInfo(epicenterStintIndex: Int): PlayerEventInfo {
            val stintHalf = GlobalState.STINT_SIZE / 2
            val epicenterRange = (GlobalState.STINT_SIZE * (epicenterStintIndex + 1))
                .let { (it - stintHalf - 1) .. (it - stintHalf) }
            val distance = min(
                abs(position - epicenterRange.first),
                abs(position - epicenterRange.last)
            )
            val damageType = when {
                distance <= stintHalf -> DamageType.Heavy
                distance <= GlobalState.STINT_SIZE + stintHalf -> DamageType.Light
                else -> DamageType.None
            }
            val epicenterDirection = when {
                abs(position - epicenterRange.first) < abs(position - epicenterRange.last) ->
                    EpicenterDirection.Front
                else -> EpicenterDirection.Back
            }
            return PlayerEventInfo(damageType, epicenterDirection, epicenterRange)
        }

        override suspend fun handle(action: GlobalEvent, currentState: GlobalState): GlobalState {
            val newState = when (action.type) {
                Type.Tornado -> currentState.updatePlayers { _, state ->
                    val playerEventInfo = state.playerEventInfo(action.epicenterStintIndex)
                    when (playerEventInfo.damageType) {
                        DamageType.None -> state
                        DamageType.Light -> state.copy(
                            position = if (playerEventInfo.isEpicenterForward) {
                                state.position + Tornado.LIGHT_DAMAGE_SHIFT
                            } else {
                                state.position - Tornado.LIGHT_DAMAGE_SHIFT
                            }.coerceIn(GlobalState.BOARD_RANGE),
                            effects = state.effects + LostFoot.create(),
                        )
                        DamageType.Heavy -> state.copy(
                            position = if (playerEventInfo.isEpicenterForward) {
                                val diff = abs(state.position - playerEventInfo.epicenterRange.first)
                                playerEventInfo.epicenterRange.last + diff
                            } else {
                                val diff = abs(state.position - playerEventInfo.epicenterRange.last)
                                playerEventInfo.epicenterRange.first - diff
                            }.coerceIn(GlobalState.BOARD_RANGE),
                            effects = state.effects + LostLeg.create(),
                        )
                    }
                }
                Type.Nuke -> currentState.updatePlayers { _, state ->
                    val playerEventInfo = state.playerEventInfo(action.epicenterStintIndex)
                    when (playerEventInfo.damageType) {
                        DamageType.None -> state
                        DamageType.Light -> state.copy(
                            position = if (playerEventInfo.isEpicenterForward) {
                                state.position - Nuke.LIGHT_DAMAGE_SHIFT
                            } else {
                                state.position + Nuke.LIGHT_DAMAGE_SHIFT
                            }.coerceIn(GlobalState.BOARD_RANGE),
                            effects = state.effects + LostFoot.create(),
                        )
                        DamageType.Heavy -> state.copy(
                            position = if (playerEventInfo.isEpicenterForward) {
                                state.position - Nuke.HEAVY_DAMAGE_SHIFT
                            } else {
                                state.position + Nuke.HEAVY_DAMAGE_SHIFT
                            }.coerceIn(GlobalState.BOARD_RANGE),
                            effects = state.effects + LostLeg.create(),
                        )
                    }
                }
            }
            return newState.copy(stateSnapshot = newState.stateSnapshot.copy(scheduledEvent = null))
        }
    }
}
