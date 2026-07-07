package com.prathamngundikere.moneta.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prathamngundikere.moneta.data.local.dao.TransactionDao
import com.prathamngundikere.moneta.data.local.entity.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class MonetaDatabase : RoomDatabase() {
    abstract val transactionDao: TransactionDao
}