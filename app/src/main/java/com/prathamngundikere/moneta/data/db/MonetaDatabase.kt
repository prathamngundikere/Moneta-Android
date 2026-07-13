package com.prathamngundikere.moneta.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AccountEntity::class,
        ItemEntity::class,
        CategoryEntity::class,
        TransactionEntity::class
               ],
    version = 4,
    exportSchema = false
)
abstract class MonetaDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
}