package com.prathamngundikere.moneta.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val merchant: String,
    val transactionDate: Long, // Stored as epoch millis for easy 1-week calculation
    val amountMoved: Double,
    val accountId: String
)