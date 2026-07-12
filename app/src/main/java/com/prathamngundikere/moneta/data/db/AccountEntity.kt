package com.prathamngundikere.moneta.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val accountType: String,
    val balance: Double,
    val isActive: Boolean
)