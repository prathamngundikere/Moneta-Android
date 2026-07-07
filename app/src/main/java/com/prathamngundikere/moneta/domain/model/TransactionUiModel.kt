package com.prathamngundikere.moneta.domain.model

data class TransactionUiModel(
    val id: String,
    val merchant: String,
    val amount: Double,
    val currency: String,
    val date: String,
    val items: List<TransactionItemUiModel>
)