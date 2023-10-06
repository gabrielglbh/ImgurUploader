package com.gabr.gabc.imguruploader.presentation.loginPage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.presentation.homePage.HomePage
import com.gabr.gabc.imguruploader.presentation.loginPage.viewModel.LoginViewModel
import com.gabr.gabc.imguruploader.presentation.shared.Constants
import com.gabr.gabc.imguruploader.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@AndroidEntryPoint
class LoginPage: ComponentActivity() {
    companion object {
        const val ACCOUNT = "ACCOUNT"
    }

    private fun extractTokenFromUri(uriFragment: String, tokenKey: String): String {
        val params = uriFragment.split("&")
        for (param in params) {
            val keyValue = param.split("=")
            if (keyValue.size == 2 && keyValue[0] == tokenKey) {
                return keyValue[1]
            }
        }
        return ""
    }

    private fun getUserData() {
        val viewModel: LoginViewModel by viewModels()
        viewModel.getUserData {
            val i = Intent(this, HomePage::class.java)
            i.putExtra(ACCOUNT, it)
            startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: LoginViewModel by viewModels()
        val data = intent.data
        if (data != null && data.scheme == Constants.REDIRECT_URL_SCHEME) {
            val uriFragment = data.fragment
            if (uriFragment != null && uriFragment.contains("access_token")) {
                val accessToken = extractTokenFromUri(uriFragment, "access_token")
                val refreshToken = extractTokenFromUri(uriFragment, "refresh_token")
                val userName = extractTokenFromUri(uriFragment, "account_username")
                viewModel.saveTokens(accessToken, refreshToken, userName)
                getUserData()
            }
        } else {
            getUserData()
        }

        setContent {
            AppTheme {
                LoginView()
            }
        }
    }

    @Composable
    fun LoginView() {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LoginForm(Modifier.padding(horizontal = 32.dp))
            }
        }
    }

    @Composable
    fun LoginForm(modifier: Modifier) {
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Button(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp).fillMaxWidth(),
                onClick = {
                    val url = Constants.AUTHORIZE_URL.toHttpUrlOrNull()
                        ?.newBuilder()
                        ?.addQueryParameter("client_id", Constants.CLIENT_ID)
                        ?.addQueryParameter("response_type", "token")
                        ?.addQueryParameter("redirect_uri", Constants.REDIRECT_URL)
                        ?.build()
                    if (url != null) {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url.toUrl().toString()))
                        startActivity(i)
                    }
                }
            ) {
                Text(stringResource(R.string.login_button))
            }
        }
    }
}
