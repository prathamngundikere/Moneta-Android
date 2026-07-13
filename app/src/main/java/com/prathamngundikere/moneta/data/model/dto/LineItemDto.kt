package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class LineItemDto(
    @SerializedName("itemId") val itemId: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("unitPrice") val unitPrice: Double
)