package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class ItemUpdateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("unit") val unit: String
)