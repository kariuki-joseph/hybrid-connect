package com.example.hybridconnect.domain.di

import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.repository.PaymentRepository
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import com.example.hybridconnect.domain.usecase.LoginCoordinator
import com.example.hybridconnect.domain.usecase.LoginUserUseCase
import com.example.hybridconnect.domain.usecase.UpdateSiteLinkUseCase
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
        siteLinkRepository: SiteLinkRepository,
        offerRepository: OfferRepository,
        paymentRepository: PaymentRepository
    ): LoginCoordinator {
        return LoginCoordinator(
            loginUserUseCase,
            siteLinkRepository,
            offerRepository,
            paymentRepository
        )
    }
}