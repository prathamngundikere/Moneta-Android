package com.prathamngundikere.moneta.domain.model

data class Item(
    val id: String,
    val createdAt: String?,
    val updatedAt: String?,
    val name: String,
    val description: String?,
    val category: Category? = null
)