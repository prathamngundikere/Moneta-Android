package com.prathamngundikere.moneta.domain.model

data class Account(
    val id: String,
    val createdAt: String?,
    val updatedAt: String?,
    val name: String,
    val accountType: AccountType,
    val balance: Double,
    val dueDate: String?,
    val currency: String,
    val isActive: Boolean
)