package com.prathamngundikere.moneta.domain.model

data class Category(
    val id: String,
    val createdAt: String?,
    val updatedAt: String?,
    val name: String,
    val parentCategory: Category? = null
)