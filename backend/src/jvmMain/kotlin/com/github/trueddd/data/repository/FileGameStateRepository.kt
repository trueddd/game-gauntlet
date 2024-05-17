package com.github.trueddd.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

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
