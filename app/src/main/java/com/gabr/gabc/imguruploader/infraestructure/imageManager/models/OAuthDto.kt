package com.gabr.gabc.imguruploader.infraestructure.imageManager.models

import com.google.gson.annotations.SerializedName

data class OAuthDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("account_username") val accountUsername: String
)
