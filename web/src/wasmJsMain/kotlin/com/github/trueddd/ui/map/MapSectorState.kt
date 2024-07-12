package com.github.trueddd.ui.map

import androidx.compose.runtime.Immutable
import com.github.trueddd.data.Participant
import com.github.trueddd.items.BoardTrap
import com.github.trueddd.map.Genre
import com.github.trueddd.map.RadioStation

@Immutable
class MapSectorState(
    val index: Int,
    val genre: Genre?,
    val players: List<Participant>,
    val traps: List<BoardTrap>,
    val radioStation: RadioStation?,
)
