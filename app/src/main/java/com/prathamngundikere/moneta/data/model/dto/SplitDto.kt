package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class SplitDto(
    @SerializedName("accountId") val accountId: String,
    @SerializedName("amount") val amount: Double
)