package com.github.trueddd.data.repository

import com.github.trueddd.data.ActionsTable
import com.github.trueddd.utils.Log
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Named(DatabaseGameStateRepository.TAG)
@Single(binds = [GameStateRepository::class])
class DatabaseGameStateRepository(
    private val database: Database,
) : BaseGameStateRepository() {

    companion object {
        const val TAG = "DatabaseGameStateRepository"
    }

    override suspend fun writeData(data: List<String>) {
        val writtenRows = suspendedTransactionAsync(Dispatchers.IO, database) {
            ActionsTable.deleteAll()
            ActionsTable.batchInsert(data) {
                this[ActionsTable.value] = it
            }
        }.await()
        Log.info(TAG, "Saved $writtenRows rows")
    }

    override suspend fun readData(): List<String> {
        return suspendedTransactionAsync(Dispatchers.IO, database) {
            ActionsTable.selectAll().map { it[ActionsTable.value] }
        }.await()
    }
}
