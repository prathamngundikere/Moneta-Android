package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionSplitDto(
    val accountId: String? = null,
    val amount: Double
)