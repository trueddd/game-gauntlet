package com.github.trueddd.utils

import bt.Bt
import bt.data.file.FileSystemStorage
import bt.dht.DHTConfig
import bt.dht.DHTModule
import bt.runtime.Config
import bt.torrent.fileselector.FilePriority
import bt.torrent.selector.RarestFirstSelector
import java.io.File

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

internal fun createTorrentClient(
    targetFileName: String,
    workingDirectory: File,
) = Bt.client()
    .config(torrentClientConfig)
    .storage(FileSystemStorage(workingDirectory))
    .magnet(Environment.GamesMagnetUri)
    .selector(RarestFirstSelector.rarest())
    .fileSelector { file ->
        val name = file.pathElements.last()
        if (name.contains(targetFileName, ignoreCase = true)) {
            FilePriority.HIGH_PRIORITY
        } else {
            FilePriority.SKIP
        }
    }
    .autoLoadModules()
    .module(dhtModule)
    .stopWhenDownloaded()
    .build()
