package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountStatementResponseDto(
    val merchant: String? = null,
    val transactionDate: String,
    val amountMoved: Double,
    val currentAccountBalance: Double? = null
)