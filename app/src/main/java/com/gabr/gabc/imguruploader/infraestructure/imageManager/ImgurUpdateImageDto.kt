package com.gabr.gabc.imguruploader.infraestructure.imageManager

import com.google.gson.annotations.SerializedName

data class ImgurUpdateImageDto(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("image") val image: String,
    @SerializedName("deletehash") val deleteHash: String = ""
)