package com.prathamngundikere.moneta.data.local.dao

import androidx.room.*
import com.prathamngundikere.moneta.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE accountId = :accountId AND transactionDate >= :oneWeekAgo ORDER BY transactionDate DESC")
    fun getRecentTransactions(accountId: String, oneWeekAgo: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions WHERE transactionDate < :oneWeekAgo")
    suspend fun deleteOldTransactions(oneWeekAgo: Long)

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}