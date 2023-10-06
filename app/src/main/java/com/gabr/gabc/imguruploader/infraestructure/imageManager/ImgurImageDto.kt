package com.gabr.gabc.imguruploader.infraestructure.imageManager

import com.google.gson.annotations.SerializedName

data class ImgurImageDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("link") val link: String,
    @SerializedName("deletehash") val deleteHash: String = ""
)