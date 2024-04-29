package com.github.trueddd.core

import com.github.trueddd.data.GameConfig
import com.github.trueddd.data.PlayersHistory
import com.github.trueddd.data.StateSnapshot
import com.github.trueddd.utils.Log
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class GameStateProviderImpl(
    private val httpClient: HttpClient,
    private val router: ServerRouter,
    private val authManager: AuthManager,
    private val appClient: AppClient,
) : GameStateProvider, CommandSender, CoroutineScope {

    companion object {
        private const val TAG = "GameStateProvider"
    }

    override val coroutineContext = Dispatchers.Default + SupervisorJob()

    private var connectionJob: Job? = null

    private val _connectionState = MutableStateFlow<SocketState>(SocketState.Disconnected())
    override val serverConnectionStateFlow: StateFlow<SocketState>
        get() = _connectionState.asStateFlow()

    private val _gameConfig = MutableStateFlow<GameConfig?>(null)
    override val gameConfig: StateFlow<GameConfig?>
        get() = _gameConfig.asStateFlow()

    private val actionsChannel = Channel<String>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun sendCommand(command: Command) {
        launch {
            Log.info(TAG, "sending `${command.value}`")
            actionsChannel.send(command.value)
        }
    }

    private val _snapshotStateFlow = MutableStateFlow<StateSnapshot?>(null)
    override val snapshotFlow: StateFlow<StateSnapshot?>
        get() = _snapshotStateFlow.asStateFlow()

    override fun initialize() {
        if (connectionJob?.isCancelled == true) {
            Log.info(TAG, "Already running")
            return
        }
        connectionJob = launch {
            _connectionState.value = SocketState.Connecting
            _gameConfig.value = appClient.getGameConfig() ?: run {
                _connectionState.value = SocketState.Disconnected()
                return@launch
            }
            if (authManager.userState.value == null) {
                val result = appClient.getStateSnapshot()
                if (result != null) {
                    _connectionState.value = SocketState.Connected
                    _snapshotStateFlow.value = result
                } else {
                    _connectionState.value = SocketState.Disconnected()
                    _snapshotStateFlow.value = null
                }
                return@launch
            }
            httpClient.webSocket(router.ws(Router.STATE)) {
                val token = authManager.savedJwtToken() ?: run {
                    close()
                    return@webSocket
                }
                outgoing.send(Frame.Text(token))
                _connectionState.value = SocketState.Connected
                launch {
                    for (action in actionsChannel) {
                        outgoing.send(Frame.Text(action))
                    }
                }
                for (frame in incoming) {
                    val textFrame = frame as? Frame.Text ?: continue
                    val data = Response.parse(textFrame.readText()) ?: continue
                    when (data) {
                        is Response.Error -> Log.error(TAG, "Error occurred: ${data.exception.message}")
                        is Response.Info -> Log.error(TAG, "Message from server: ${data.message}")
                        is Response.State -> _snapshotStateFlow.value = data.snapshot
                        else -> continue
                    }
                }
                if (this.coroutineContext.job.isActive) {
                    val reason = closeReason.await()?.message ?: "Unknown reason"
                    if (reason == Response.ErrorCode.AuthError || reason == Response.ErrorCode.TokenExpired) {
                        authManager.logout()
                    }
                    cancel(reason)
                }
            }
        }
    }

    override fun playersHistoryFlow(): Flow<PlayersHistory?> {
        return if (authManager.userState.value == null) {
            flow { emit(appClient.getPlayersHistory()) }
        } else {
            appClient.getPlayersHistoryFlow()
        }
    }
}
