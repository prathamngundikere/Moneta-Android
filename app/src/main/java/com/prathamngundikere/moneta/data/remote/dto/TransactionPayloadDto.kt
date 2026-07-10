package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionPayloadDto(
    val merchant: String,
    val transactionDate: String,
    val notes: String? = null,
    val splits: List<TransactionSplitDto>,
    val lineItems: List<TransactionLineItemDto>
)