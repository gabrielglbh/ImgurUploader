package com.gabr.gabc.imguruploader.di

import android.content.ContentResolver
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentResolverProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun resolver(): ContentResolver = context.contentResolver
}