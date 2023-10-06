package com.gabr.gabc.imguruploader.di

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getPref(): SharedPreferences {
        return context.getSharedPreferences("GENERAL", Context.MODE_PRIVATE)
    }
}