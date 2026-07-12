package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class AccountDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("accountType") val accountType: String,
    @SerializedName("balance") val balance: Double,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("dueDate") val dueDate: String?,
    @SerializedName("isActive") val isActive: Boolean
)