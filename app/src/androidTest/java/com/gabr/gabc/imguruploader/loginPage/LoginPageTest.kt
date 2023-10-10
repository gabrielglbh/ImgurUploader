package com.gabr.gabc.imguruploader.loginPage

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gabr.gabc.imguruploader.presentation.loginPage.LoginPage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginPageTest {
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
    fun loginButton() {
        onView(withText("Iniciar sesión")).perform(click())
    }
}