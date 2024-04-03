package com.github.trueddd

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.trueddd.core.AppClient
import com.github.trueddd.core.AuthManager
import com.github.trueddd.core.SocketState
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.di.KoinIntegration
import com.github.trueddd.di.get
import com.github.trueddd.theme.Colors
import com.github.trueddd.theme.DarkColors
import com.github.trueddd.ui.*
import com.github.trueddd.ui.profile.ProfileScreen
import com.github.trueddd.ui.rules.Rules
import com.github.trueddd.ui.wheels.Wheels
import kotlinx.coroutines.delay

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
        MaterialTheme(
            colorScheme = DarkColors,
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
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
    val destinations = Destination.all()
    var destination by remember { mutableStateOf(destinations.first()) }
    val authManager = remember { get<AuthManager>() }
    val user by authManager.userState.collectAsState()
    LaunchedEffect(Unit) {
        val arguments = authManager.receiveHashParameters()
        authManager.auth(arguments)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        InfoPanel(
            socketState = socketState,
            participant = user,
            modifier = Modifier
                .fillMaxWidth()
        )
        TopPanel(
            currentDestination = destination,
            destinations = destinations,
            onDestinationChanged = { destination = it },
            participant = user,
            modifier = Modifier
                .fillMaxWidth()
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            when (destination) {
                is Destination.Rules -> {
                    Rules(
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                is Destination.Map -> {
                    if (globalState != null) {
                        Map(
                            globalState = globalState
                        )
                    }
                }
                is Destination.Dashboard -> {
                    if (globalState != null) {
                        Dashboard(
                            globalState = globalState,
                            socketState = socketState,
                            participant = user,
                            modifier = Modifier
                        )
                    }
                }
                is Destination.Games -> {
                    Archives(
                        modifier = Modifier
                    )
                }
                is Destination.Profile -> {
                    ProfileScreen(
                        currentParticipant = user,
                        globalState = globalState,
                        modifier = Modifier
                    )
                }
                is Destination.Wheels -> {
                    if (user != null) {
                        Wheels(
                            participant = user!!,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopPanel(
    currentDestination: Destination,
    destinations: List<Destination>,
    participant: Participant?,
    modifier: Modifier = Modifier,
    onDestinationChanged: (Destination) -> Unit = {},
) {
    NavigationBar(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = { onDestinationChanged(destination) },
                enabled = !destination.isPrivate || participant != null,
                icon = {
                    Icon(
                        imageVector = if (currentDestination == destination) {
                            destination.icon
                        } else {
                            destination.disabledIcon
                        },
                        contentDescription = null,
                    )
                },
                label = {
                    Text(destination.name)
                },
                modifier = Modifier
                    .pointerHoverIcon(
                        if (!destination.isPrivate || participant != null) {
                            PointerIcon.Hand
                        } else {
                            PointerIcon.Default
                        }
                    )
            )
        }
    }
}

@Composable
private fun InfoPanel(
    socketState: SocketState,
    participant: Participant?,
    modifier: Modifier = Modifier
) {
    var infoPanelVisible by remember { mutableStateOf(false) }
    LaunchedEffect(socketState) {
        if (socketState is SocketState.Connected) {
            delay(300L)
        }
        infoPanelVisible = socketState !is SocketState.Connected && participant != null
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
                color = Colors.White,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
