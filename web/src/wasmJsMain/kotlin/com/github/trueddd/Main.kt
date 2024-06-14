package com.github.trueddd

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import com.github.trueddd.core.AuthManager
import com.github.trueddd.core.GameStateProvider
import com.github.trueddd.core.SocketState
import com.github.trueddd.data.Participant
import com.github.trueddd.di.KoinIntegration
import com.github.trueddd.di.get
import com.github.trueddd.theme.Colors
import com.github.trueddd.theme.DarkColors
import com.github.trueddd.theme.OpenSansFont
import com.github.trueddd.ui.Dashboard
import com.github.trueddd.ui.Destination
import com.github.trueddd.ui.Map
import com.github.trueddd.ui.profile.ProfileScreen
import com.github.trueddd.ui.res.FontTypography
import com.github.trueddd.ui.rules.AllWheelItems
import com.github.trueddd.ui.rules.Rules
import com.github.trueddd.ui.wheels.Wheels
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    KoinIntegration.start()
    CanvasBasedWindow("AGG2", canvasElementId = "canvas") {
        val gameStateProvider = remember { get<GameStateProvider>() }
        val socketState by gameStateProvider.serverConnectionStateFlow.collectAsState()
        LaunchedEffect(Unit) {
            gameStateProvider.initialize()
        }
        MaterialTheme(
            typography = FontTypography(OpenSansFont()),
            colorScheme = DarkColors,
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
                App(
                    socketState = socketState
                )
            }
        }
    }
}

@Composable
private fun App(
    socketState: SocketState,
) {
    val destinations = Destination.all()
    var destination by remember { mutableStateOf(destinations.first()) }
    val authManager = remember { get<AuthManager>() }
    val gameStateProvider = remember { get<GameStateProvider>() }
    val user by authManager.userState.collectAsState()
    val stateSnapshot by gameStateProvider.snapshotFlow.collectAsState()
    val gameConfig by gameStateProvider.gameConfig.collectAsState()
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
                is Destination.Items -> {
                    AllWheelItems(
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                is Destination.Rules -> {
                    Rules(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is Destination.Map -> {
                    if (gameConfig != null) {
                        Map(
                            gameConfig = gameConfig!!,
                            stateSnapshot = stateSnapshot,
                        )
                    }
                }
                is Destination.Dashboard -> {
                    if (gameConfig != null && stateSnapshot != null) {
                        Dashboard(
                            gameConfig = gameConfig!!,
                            stateSnapshot = stateSnapshot!!,
                            socketState = socketState,
                            participant = user,
                            modifier = Modifier
                        )
                    }
                }
                is Destination.Profile -> {
                    if (gameConfig != null) {
                        ProfileScreen(
                            currentParticipant = user,
                            gameConfig = gameConfig!!,
                            stateSnapshot = stateSnapshot,
                            modifier = Modifier
                        )
                    }
                }
                is Destination.Wheels -> {
                    if (user != null && gameConfig != null) {
                        Wheels(
                            player = user!!,
                            gameConfig = gameConfig!!,
                            currentPlayerState = stateSnapshot?.playersState?.get(user!!.name),
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
    val padding = 24.dp
    Row(
        modifier = modifier
            .padding(top = padding, start = padding, end = padding)
            .height(72.dp)
            .background(MaterialTheme.colorScheme.onSecondary, RoundedCornerShape(50))
    ) {
        destinations.forEach { destination ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(
                        color = if (currentDestination == destination) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSecondary
                        },
                        shape = RoundedCornerShape(50)
                    )
                    .clip(RoundedCornerShape(50))
                    .clickable(enabled = !destination.requireAuth || participant != null) {
                        onDestinationChanged(destination)
                    }
                    .pointerHoverIcon(
                        if (!destination.requireAuth || participant != null) {
                            PointerIcon.Hand
                        } else {
                            PointerIcon.Default
                        }
                    )
            ) {
                val destinationContentColor = when {
                    destination.requireAuth && participant == null -> MaterialTheme.colorScheme.outline
                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                }
                Icon(
                    imageVector = if (currentDestination == destination) {
                        destination.icon
                    } else {
                        destination.disabledIcon
                    },
                    contentDescription = null,
                    tint = destinationContentColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = destination.name,
                    color = destinationContentColor
                )
            }
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
