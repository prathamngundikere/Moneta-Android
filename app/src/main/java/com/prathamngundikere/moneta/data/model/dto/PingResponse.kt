package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class PingResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("timestamp")
    val timestamp: String
)