package com.gabr.gabc.imguruploader.domain.imageManager.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImgurImage(
    val title: String?,
    val description: String?,
    val deleteHash: String = "",
    val link: Uri,
) : Parcelable