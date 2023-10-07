package com.gabr.gabc.imguruploader.infraestructure.imageManager.models

import com.google.gson.annotations.SerializedName

data class AccountDto(
    @SerializedName("url") val username: String,
    @SerializedName("avatar") val avatar: String,
)
