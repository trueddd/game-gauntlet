package com.github.trueddd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.trueddd.core.AppClient
import com.github.trueddd.data.GlobalState
import com.github.trueddd.di.module
import com.github.trueddd.theme.Colors
import com.github.trueddd.ui.ActionsBoardW
import com.github.trueddd.ui.ArchivesW
import com.github.trueddd.ui.MapW
import com.github.trueddd.ui.StateTableW
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val koinApp = startKoin {
        modules(module)
    }
    CanvasBasedWindow("AGG2", canvasElementId = "canvas") {
        val appClient = remember { koinApp.koin.get<AppClient>() }
        DisposableEffect(Unit) {
            appClient.start()
            onDispose {
                appClient.stop()
            }
        }
        val state by appClient.globalState.collectAsState()
        if (state != null) {
            MaterialTheme {
                CompositionLocalProvider(LocalContentColor provides Colors.Text) {
                    App(
                        globalState = state!!,
                        onActionSent = { appClient.sendAction(it) },
                        onSearchRequested = { appClient.searchGame(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun App(
    globalState: GlobalState,
    onActionSent: (String) -> Unit = {},
    onSearchRequested: (String) -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Background)
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ActionsBoardW(globalState, onActionSent)
            StateTableW(globalState)
            ArchivesW(onSearchRequested)
        }
        MapW(globalState)
    }
}
