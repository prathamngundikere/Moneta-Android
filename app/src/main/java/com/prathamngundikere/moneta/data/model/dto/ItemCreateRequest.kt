package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class ItemCreateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)