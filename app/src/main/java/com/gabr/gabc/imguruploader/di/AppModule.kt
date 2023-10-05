package com.gabr.gabc.imguruploader.di

import com.gabr.gabc.imguruploader.domain.auth.AuthRepository
import com.gabr.gabc.imguruploader.infraestructure.auth.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {
    @Binds
    @ViewModelScoped
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository
}