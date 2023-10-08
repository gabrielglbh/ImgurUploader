package com.gabr.gabc.imguruploader.presentation.shared

class Constants {
    companion object {
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
        const val REFRESH_TOKEN = "REFRESH_TOKEN"
        const val ACCOUNT_NAME = "ACCOUNT_NAME"
        const val REDIRECT_URL_SCHEME = "com.gabr.gabc.imguruploader.auth"
        const val REDIRECT_URL = "$REDIRECT_URL_SCHEME://callback"
        const val AUTHORIZE_URL = "https://api.imgur.com/oauth2/authorize"

        const val CLIENT_ID = ""
        const val SECRET_CLIENT_ID = ""
    }
}