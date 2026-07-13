package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class AccountUpdateRequest(
    @SerializedName("name") val name: String,
    @SerializedName("accountType") val accountType: String,
    @SerializedName("isActive") val isActive: Boolean
)