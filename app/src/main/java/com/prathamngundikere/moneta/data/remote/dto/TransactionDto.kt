package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val merchant: String,
    val transactionDate: String,
    val totalAmount: Double,
    val notes: String? = null,
    val splits: List<TransactionSplitDto> = emptyList(),
    val lineItems: List<TransactionLineItemDto> = emptyList()
)