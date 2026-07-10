package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecurringTransactionDto(
    val id: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val name: String,
    val frequency: String,
    val nextExecutionDate: String,
    val isActive: Boolean = true,
    val payload: TransactionPayloadDto
)