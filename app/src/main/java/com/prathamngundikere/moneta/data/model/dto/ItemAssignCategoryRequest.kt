package com.prathamngundikere.moneta.data.model.dto

import com.google.gson.annotations.SerializedName

data class ItemAssignCategoryRequest(
    @SerializedName("itemIds") val itemIds: List<String>,
    @SerializedName("categoryId") val categoryId: String?
)