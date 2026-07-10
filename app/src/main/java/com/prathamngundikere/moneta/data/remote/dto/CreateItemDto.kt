package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateItemDto(
    val name: String,
    val description: String? = null,
    val category: ItemCategoryDto
)