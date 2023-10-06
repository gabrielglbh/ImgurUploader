package com.gabr.gabc.imguruploader.infraestructure.imageManager.models

data class ImgurImageDto(
    val title: String,
    val description: String,
    val image: String,
    val type: String = "base64"
)