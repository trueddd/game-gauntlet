package com.github.trueddd.core

import bt.Bt
import bt.data.file.FileSystemStorage
import bt.dht.DHTConfig
import bt.dht.DHTModule
import bt.runtime.Config
import bt.torrent.fileselector.FilePriority
import bt.torrent.selector.RarestFirstSelector
import com.github.trueddd.utils.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Factory(binds = [GameLoader::class])
class TorrentGameLoaderImpl : GameLoader {

    private val currentDir = Environment.GamesDirectory
        .resolve(UUID.randomUUID().toString())
        .also { it.mkdir() }

    private val torrentClientConfig = object : Config() {
        override fun getNumOfHashingThreads(): Int {
            return Runtime.getRuntime().availableProcessors() * 2
        }
    }

    private val dhtModule = DHTModule(object : DHTConfig() {
        override fun shouldUseRouterBootstrap(): Boolean {
            return true
        }
    })

    private fun getClientBuilder() = Bt.client()
        .config(torrentClientConfig)
        .storage(FileSystemStorage(currentDir))
        .magnet(Environment.GamesMagnetUri)
        .selector(RarestFirstSelector.rarest())
        .autoLoadModules()
        .module(dhtModule)
        .stopWhenDownloaded()

    override suspend fun loadGame(gameName: String): File? {
        val client = getClientBuilder()
            .fileSelector { file ->
                val name = file.pathElements.last()
                if (name.contains(gameName, ignoreCase = true)) {
                    FilePriority.HIGH_PRIORITY
                } else {
                    FilePriority.SKIP
                }
            }
            .build()
        suspendCoroutine { con ->
            client.startAsync({
                if (it.piecesRemaining == 0) {
                    client.stop()
                    con.resume(Unit)
                }
            }, 1000L).join()
        }
        val downloadedFile = currentDir.walk()
            .filter { it.isFile }
            .filter { it.nameWithoutExtension.equals(gameName, ignoreCase = true) }
            .firstOrNull() ?: return null
        val resultFile = Environment.GamesDirectory.resolve(downloadedFile.name)
        withContext(Dispatchers.IO) {
            Files.copy(downloadedFile.toPath(), resultFile.toPath())
        }
        currentDir.deleteRecursively()
        return resultFile
    }
}
