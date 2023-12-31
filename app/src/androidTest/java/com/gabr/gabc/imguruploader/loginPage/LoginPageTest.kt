package com.gabr.gabc.imguruploader.loginPage

import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.ext.junit.rules.ActivityScenarioRule
import arrow.core.Either
import com.gabr.gabc.imguruploader.di.AppModule
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerFailure
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.domain.imageManager.models.ImgurImage
import com.gabr.gabc.imguruploader.domain.imageManager.models.OAuth
import com.gabr.gabc.imguruploader.presentation.loginPage.LoginPage
import com.gabr.gabc.imguruploader.presentation.shared.Constants
import dagger.Binds
import dagger.Module
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.testing.TestInstallIn
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

@UninstallModules(AppModule::class)
@HiltAndroidTest
class LoginPageTest {
    @Module
    @TestInstallIn(
        components = [ViewModelComponent::class],
        replaces = [AppModule::class]
    )
    interface FakeAppModule {
        @Binds
        @ViewModelScoped
        fun bindImageManagerRepository(fake: FakeImageManageRepository): ImageManagerRepository
    }

    inner class FakeImageManageRepository : ImageManagerRepository {
        override suspend fun getSession(
            refreshToken: String,
            clientId: String,
            clientSecret: String,
            clientType: String
        ): Either<ImageManagerFailure, OAuth> {
            return Either.Right(OAuth("accessToken", "refreshToken", "username"))
        }

        override suspend fun getUserData(userName: String): Either<ImageManagerFailure, Account> {
            return Either.Left(ImageManagerFailure.UserRetrievalFailed(""))
        }

        override suspend fun uploadImage(
            title: String,
            description: String,
            file: File
        ): Either<ImageManagerFailure, Unit> {
            return Either.Right(Unit)
        }

        override suspend fun deleteImage(
            userName: String,
            deleteHash: String
        ): Either<ImageManagerFailure, Unit> {
            return Either.Right(Unit)
        }

        override suspend fun getImages(): Either<ImageManagerFailure, List<ImgurImage>> {
            return Either.Right(listOf(ImgurImage("title", "", "", Uri.parse("https://placebear.com/g/200/200"))))
        }
    }

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @JvmField
    @Rule(order = 1)
    val scenarioRule = ActivityScenarioRule(LoginPage::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun loginButton_SendsCorrectUri() {
        val buttonText = "Iniciar sesión"
        val url = Constants.AUTHORIZE_URL.toHttpUrlOrNull()
            ?.newBuilder()
            ?.addQueryParameter("client_id", Constants.CLIENT_ID)
            ?.addQueryParameter("response_type", "token")
            ?.addQueryParameter("redirect_uri", Constants.REDIRECT_URL)
            ?.build()
        onView(withText(buttonText)).check(matches(isDisplayed()))
        onView(withText(buttonText)).perform(click())
        intended(allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData(Uri.parse(url?.toUrl().toString()))
        ))
    }
}