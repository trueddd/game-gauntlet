package com.github.trueddd.core

import com.github.trueddd.data.Participant

interface ParticipantProvider {

    val participants: Set<Participant>

    operator fun get(name: String): Participant?
}
