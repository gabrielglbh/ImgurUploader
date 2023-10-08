package com.gabr.gabc.imguruploader.infraestructure.imageManager.models

import com.google.gson.annotations.SerializedName

data class ImgurResponse<T>(
    @SerializedName("data") val data: T,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: Int,
)