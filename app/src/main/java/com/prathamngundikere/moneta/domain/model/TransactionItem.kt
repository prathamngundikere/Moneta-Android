package com.prathamngundikere.moneta.domain.model

data class TransactionItem(
    val id: String,
    val merchant: String,
    val transactionDate: String,
    val totalAmount: Double,
    val notes: String?,
    val splits: List<TransactionSplit>,
    val lineItems: List<TransactionLineItem>
)