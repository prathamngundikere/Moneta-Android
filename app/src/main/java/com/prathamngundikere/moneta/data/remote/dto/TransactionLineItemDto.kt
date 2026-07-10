package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionLineItemDto(
    val itemId: String? = null,
    val quantity: Double,
    val unitPrice: Double,
    val lineTotal: Double? = null
)