package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountDto(
    val name: String,
    val accountType: String,
    val balance: Double,
    val currency: String,
    val dueDate: String? = null,
    val isActive: Boolean = true,
    @SerialName("is_active") val isActiveSnakeCase: Boolean = true
)