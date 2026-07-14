package com.prathamngundikere.moneta.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val unit: String,
    val categoryId: String?,
    val categoryName: String?,
    val createdAt: String,
    val updatedAt: String
)