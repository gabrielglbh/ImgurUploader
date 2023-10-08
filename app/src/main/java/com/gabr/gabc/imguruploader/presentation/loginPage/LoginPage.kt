package com.gabr.gabc.imguruploader.presentation.loginPage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.gabr.gabc.imguruploader.databinding.LoginPageLayoutBinding
import com.gabr.gabc.imguruploader.presentation.homePage.HomePage
import com.gabr.gabc.imguruploader.presentation.loginPage.viewModel.LoginViewModel
import com.gabr.gabc.imguruploader.presentation.shared.Constants
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@AndroidEntryPoint
class LoginPage: AppCompatActivity() {
    companion object {
        const val ACCOUNT = "ACCOUNT"
    }

    private lateinit var binding: LoginPageLayoutBinding

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

    private fun getUserData(viewModel: LoginViewModel) {
        viewModel.getUserData {
            val i = Intent(this, HomePage::class.java)
            i.putExtra(ACCOUNT, it)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
                getUserData(viewModel)
            }
        } else {
            getUserData(viewModel)
        }

        binding = LoginPageLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.login.setOnClickListener {
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

        viewModel.isLoading.observe(this) {
            binding.loadingLayout.loading.visibility = if (it) { View.VISIBLE } else { View.GONE }
        }
    }
}
