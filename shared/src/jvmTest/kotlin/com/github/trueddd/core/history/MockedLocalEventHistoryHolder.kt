package com.github.trueddd.core.history

import com.github.trueddd.core.ActionHandlerRegistry

class MockedLocalEventHistoryHolder(actionHandlerRegistry: ActionHandlerRegistry) :
    LocalEventHistoryHolder(actionHandlerRegistry) {

    override val saveLocation = ".\\src\\jvmTest\\resources\\history"

    override val overwrite = true
}
