package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class TransactionPayloadDto(
    @SerializedName("merchant") val merchant: String,
    @SerializedName("transactionDate") val transactionDate: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("splits") val splits: List<SplitDto>,
    @SerializedName("lineItems") val lineItems: List<LineItemDto> = emptyList()
)