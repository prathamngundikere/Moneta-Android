package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class CurrencyDto(
    @SerializedName("code") val code: String,
    @SerializedName("symbol") val symbol: String
)