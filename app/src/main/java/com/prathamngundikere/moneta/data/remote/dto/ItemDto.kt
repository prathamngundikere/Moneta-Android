package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val id: String? = null,
    val name: String,
    val description: String? = null
)