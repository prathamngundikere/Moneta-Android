package com.prathamngundikere.moneta.data.model.dto
import com.google.gson.annotations.SerializedName

data class ItemHistoryDto(
    @SerializedName("merchant") val merchant: String,
    @SerializedName("purchaseDate") val purchaseDate: String,
    @SerializedName("quantity") val quantity: Double,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("totalPaid") val totalPaid: Double
)