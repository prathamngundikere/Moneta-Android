package com.prathamngundikere.moneta.data.model.dto
import com.google.gson.annotations.SerializedName

data class AccountStatementDto(
    @SerializedName("merchant") val merchant: String,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("amountMoved") val amountMoved: Double,
    @SerializedName("currentAccountBalance") val currentAccountBalance: Double
)