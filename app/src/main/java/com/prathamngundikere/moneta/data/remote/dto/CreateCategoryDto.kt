package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCategoryDto(
    val name: String,
    val parentCategory: CategoryParentDto? = null
)