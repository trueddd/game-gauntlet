package com.github.trueddd

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.trueddd.core.AppClient
import com.github.trueddd.core.SocketState
import com.github.trueddd.data.GlobalState
import com.github.trueddd.di.KoinIntegration
import com.github.trueddd.di.get
import com.github.trueddd.theme.Colors
import com.github.trueddd.ui.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    KoinIntegration.start()
    CanvasBasedWindow("AGG2", canvasElementId = "canvas") {
        val appClient = remember { get<AppClient>() }
        DisposableEffect(Unit) {
            appClient.start()
            onDispose {
                appClient.stop()
            }
        }
        val state by appClient.globalState.collectAsState()
        val socketState by appClient.connectionState.collectAsState()
        MaterialTheme {
            CompositionLocalProvider(LocalContentColor provides Colors.Text) {
                App(
                    globalState = state,
                    socketState = socketState,
                )
            }
        }
    }
}

@Composable
private fun App(
    globalState: GlobalState?,
    socketState: SocketState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Background)
    ) {
        InfoPanel(
            socketState,
            modifier = Modifier
                .fillMaxWidth()
        )
        val destinations = Destination.all()
        var destination by remember { mutableStateOf(destinations.first()) }
        TopPanel(
            destinations = destinations,
            onDestinationChanged = { destination = it },
            modifier = Modifier
                .fillMaxWidth()
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (globalState != null) {
                when (destination) {
                    is Destination.Rules -> {
                        Rules(
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                    is Destination.Map -> {
                        Map(
                            globalState = globalState
                        )
                    }
                    is Destination.Dashboard -> {
                        Dashboard(
                            globalState = globalState,
                            socketState = socketState,
                            modifier = Modifier
                        )
                    }
                    is Destination.Games -> {
                        Archives(
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopPanel(
    destinations: List<Destination>,
    modifier: Modifier = Modifier,
    onDestinationChanged: (Destination) -> Unit = {},
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .background(Colors.DarkBackground)
    ) {
        destinations.forEachIndexed { index, destination ->
            if (index != 0) {
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(Colors.SecondaryBackground)
                )
            }
            val interactionSource = remember { MutableInteractionSource() }
            val hovered by interactionSource.interactions
                .filter { it is HoverInteraction }
                .map { it is HoverInteraction.Enter }
                .collectAsState(initial = false)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .background(if (hovered) Colors.SecondaryBackground else Colors.DarkBackground)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .hoverable(interactionSource)
                    .clickable { onDestinationChanged(destination) }
            ) {
                Text(
                    text = destination.name,
                    modifier = Modifier
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoPanel(
    socketState: SocketState,
    modifier: Modifier = Modifier
) {
    var infoPanelVisible by remember { mutableStateOf(false) }
    LaunchedEffect(socketState) {
        if (socketState is SocketState.Connected) {
            delay(300L)
        }
        infoPanelVisible = socketState !is SocketState.Connected
    }
    AnimatedVisibility(
        visible = infoPanelVisible,
        modifier = modifier
            .background(
                when (socketState) {
                    is SocketState.Connected -> Colors.Success
                    is SocketState.Connecting -> Colors.Warning
                    is SocketState.Disconnected -> Colors.Error
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = when (socketState) {
                    is SocketState.Disconnected -> "Нет подключения к серверу"
                    is SocketState.Connecting -> "Подключение к серверу..."
                    is SocketState.Connected -> "Подключение установлено"
                },
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
