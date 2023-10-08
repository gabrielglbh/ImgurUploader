package com.gabr.gabc.imguruploader.domain.imageManager.models

data class OAuth(
    val accessToken: String,
    val refreshToken: String,
    val accountUsername: String
)
