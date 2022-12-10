package com.github.trueddd

import com.github.trueddd.core.InputParser
import com.github.trueddd.core.ItemRoller
import com.github.trueddd.core.StateHolder
import com.github.trueddd.di.getActionGeneratorSet
import com.github.trueddd.di.getItemFactorySet

internal fun provideInputParser() = InputParser(
    getActionGeneratorSet(
        ItemRoller(getItemFactorySet()),
        StateHolder(),
    )
)
