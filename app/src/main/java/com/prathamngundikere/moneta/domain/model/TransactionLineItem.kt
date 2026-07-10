package com.prathamngundikere.moneta.domain.model

data class TransactionLineItem(
    val itemId: String,
    val quantity: Double,
    val unitPrice: Double,
    val lineTotal: Double
)