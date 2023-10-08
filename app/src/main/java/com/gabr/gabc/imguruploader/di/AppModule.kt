package com.gabr.gabc.imguruploader.di

import com.gabr.gabc.imguruploader.domain.imageManager.ImageManagerRepository
import com.gabr.gabc.imguruploader.infraestructure.imageManager.ImageManagerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface AppModule {
    @Binds
    @ViewModelScoped
    fun bindImageManagerRepository(repository: ImageManagerRepositoryImpl): ImageManagerRepository
}