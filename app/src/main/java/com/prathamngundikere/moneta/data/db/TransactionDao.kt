package com.prathamngundikere.moneta.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions ORDER BY transactionDate DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    // Keeps only a reasonable amount (e.g., 7 days)
    @Query("DELETE FROM transactions WHERE date(transactionDate) < date(:cutoffDate)")
    suspend fun deleteOlderThan(cutoffDate: String)
}