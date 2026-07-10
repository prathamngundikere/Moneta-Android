package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRecurringTransactionDto(
    val name: String,
    val frequency: String,
    val nextExecutionDate: String,
    val isActive: Boolean = true,
    @SerialName("is_active") val isActiveSnakeCase: Boolean = true,
    val payload: TransactionPayloadDto
)