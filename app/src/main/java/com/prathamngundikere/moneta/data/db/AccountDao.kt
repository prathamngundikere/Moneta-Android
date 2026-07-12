package com.prathamngundikere.moneta.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)

    @Query("SELECT * FROM accounts")
    fun getAllAccountsFlow(): Flow<List<AccountEntity>>

    @Query("SELECT COUNT(id) FROM accounts")
    suspend fun getAccountCount(): Int
}