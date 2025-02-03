package com.example.hybridconnect.domain.di

import com.example.hybridconnect.domain.usecase.LoginCoordinator
import com.example.hybridconnect.domain.usecase.LoginUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoordinatorModule {
    @Provides
    @Singleton
    fun provideLoginCoordinator(
        loginUserUseCase: LoginUserUseCase,
    ): LoginCoordinator {
        return LoginCoordinator(
            loginUserUseCase
        )
    }
}