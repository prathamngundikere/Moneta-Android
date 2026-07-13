package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class AccountCreateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("accountType") val accountType: String,
    @SerializedName("balance") val balance: Double,
    @SerializedName("isActive") val isActive: Boolean = true
)