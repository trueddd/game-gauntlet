package com.github.trueddd.data.repository

import com.github.trueddd.di.CoroutineDispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.io.File

@Named(FileGameStateRepository.TAG)
@Single(binds = [GameStateRepository::class])
class FileGameStateRepository(
    private val file: File,
    private val dispatchers: CoroutineDispatchers,
) : BaseGameStateRepository() {

    companion object {
        const val TAG = "FileGameStateRepository"
    }

    init {
        file.createNewFile()
    }

    override suspend fun writeData(data: List<String>) {
        withContext(dispatchers.io) {
            file.writeText(data.joinToString("\n"))
        }
    }

    override suspend fun readData(): List<String> {
        return withContext(dispatchers.io) {
            file.readLines()
        }
    }
}
