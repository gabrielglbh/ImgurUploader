package com.gabr.gabc.imguruploader.presentation.homePage.viewModel

import android.net.Uri

data class ImgFormState(
    val link: Uri? = Uri.EMPTY,
    val title: String = "",
    val description: String = "",
    val error: String = ""
    )
