package com.gabr.gabc.imguruploader.di

import com.gabr.gabc.imguruploader.domain.http.HttpRepository
import com.gabr.gabc.imguruploader.infraestructure.http.HttpRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface HttpModule {
    @Binds
    fun bindHttpRepository(repository: HttpRepositoryImpl): HttpRepository
}