package com.prathamngundikere.moneta.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val merchant: String,
    val transactionDate: String,
    val totalAmount: Double,
    val notes: String?
)