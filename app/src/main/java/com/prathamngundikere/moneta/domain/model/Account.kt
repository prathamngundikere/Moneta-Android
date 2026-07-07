package com.prathamngundikere.moneta.domain.model

data class Account(
    val id: String,
    val name: String,
    val accountType: AccountType,
    val balance: Double,
    val currency: String
)