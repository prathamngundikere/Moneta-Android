package com.prathamngundikere.moneta.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: String? = null,
    val name: String,
    val accountType: String,
    val balance: Double,
    val currency: String
)