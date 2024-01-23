package com.github.trueddd.utils

import com.github.trueddd.actions.Action

class StateModificationException(val action: Action, cause: String) : Exception("State modification failed - $cause")
