package com.gabr.gabc.imguruploader.homePage

import android.net.Uri
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import arrow.core.Either
import com.gabr.gabc.imguruploader.R
import com.gabr.gabc.imguruploader.di.AppModule
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerFailure
import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.domain.imageManager.models.Account
import com.gabr.gabc.imguruploader.domain.imageManager.models.ImgurImage
import com.gabr.gabc.imguruploader.domain.imageManager.models.OAuth
import com.gabr.gabc.imguruploader.presentation.homePage.HomePage
import dagger.Binds
import dagger.Module
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.testing.TestInstallIn
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File


@UninstallModules(AppModule::class)
@HiltAndroidTest
class HomePageTest {
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
            return Either.Right(Account("", Uri.EMPTY))
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
    val scenarioRule = ActivityScenarioRule(HomePage::class.java)

    private var device: UiDevice? = null

    @Before
    fun setUp() {
        hiltRule.inject()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun homePage_AssertImages() {
        onView(withId(R.id.imgur_images)).check(matches(isDisplayed()))
        onView(withId(R.id.imgur_images)).check(matches(hasDescendant(allOf(
            isAssignableFrom(ImageView::class.java),
            isDisplayed()
        ))))
    }

    @Test
    fun homePage_AddPhotoToList() {
        onView(withId(R.id.add_image)).perform(click())
        onView(withId(R.id.add_from_gallery)).perform(click())
        Thread.sleep(2000)
        val imageSelector = UiSelector().resourceId("com.android.gallery3d:id/gl_root_view")
        imageSelector.childSelector(UiSelector().index(0))
        Thread.sleep(2000)
        onView(withId(R.id.image_to_upload)).check(matches(isDisplayed()))
    }
}