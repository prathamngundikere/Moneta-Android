package com.prathamngundikere.moneta.domain.model

data class RecurringTransaction(
    val id: String,
    val name: String,
    val frequency: Frequency,
    val nextExecutionDate: String,
    val isActive: Boolean
)