package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class CategoryCreateRequest(
    @SerializedName("name") val name: String
)