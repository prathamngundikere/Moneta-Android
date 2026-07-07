package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String? = null,
    val name: String
)
