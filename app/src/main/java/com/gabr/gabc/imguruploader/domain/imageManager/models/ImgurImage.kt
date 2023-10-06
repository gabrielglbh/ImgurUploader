package com.gabr.gabc.imguruploader.domain.imageManager.models

import android.net.Uri

data class ImgurImage(
    val title: String,
    val description: String,
    val deleteHash: String = "",
    val link: Uri,
)