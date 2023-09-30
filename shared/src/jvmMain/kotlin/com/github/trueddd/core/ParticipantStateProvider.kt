package com.github.trueddd.core

import com.github.trueddd.data.Participant
import com.github.trueddd.data.PlayerState

interface ParticipantStateProvider {

    operator fun get(participant: Participant): PlayerState
}
