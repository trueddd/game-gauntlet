package com.github.trueddd.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.File

@Named(FileGameStateRepository.TAG)
@Single(binds = [GameStateRepository::class])
class FileGameStateRepository(
    private val file: File,
) : BaseGameStateRepository() {

    companion object {
        const val TAG = "FileGameStateRepository"
    }

    init {
        file.createNewFile()
    }

    override suspend fun writeData(data: List<String>) {
        withContext(Dispatchers.IO) {
            file.writeText(data.joinToString("\n"))
        }
    }

    override suspend fun readData(): List<String> {
        return withContext(Dispatchers.IO) {
            file.readLines()
        }
    }
}
