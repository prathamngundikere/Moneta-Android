package com.prathamngundikere.moneta.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AccountEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MonetaDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}