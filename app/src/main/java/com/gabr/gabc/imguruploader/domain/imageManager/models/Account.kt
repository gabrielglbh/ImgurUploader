package com.gabr.gabc.imguruploader.domain.imageManager.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    val username: String,
    val avatar: Uri,
) : Parcelable
