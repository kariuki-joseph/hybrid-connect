package com.example.hybridconnect.domain.di

import android.content.Context
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.SettingsRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.services.DefaultMessageExtractor
import com.example.hybridconnect.domain.services.SiteLinkMessageExtractor
import com.example.hybridconnect.domain.services.SmsProcessor
import com.example.hybridconnect.domain.services.TillMessageExtractor
import com.example.hybridconnect.domain.services.interfaces.MessageExtractor
import com.example.hybridconnect.domain.usecase.ExtractMessageDetailsUseCase
import com.example.hybridconnect.domain.usecase.ForwardMessagesUseCase
import com.example.hybridconnect.domain.usecase.GetAppStatusUseCase
import com.example.hybridconnect.domain.usecase.GetOfferByPriceUseCase
import com.example.hybridconnect.domain.usecase.LoginUserUseCase
import com.example.hybridconnect.domain.usecase.LogoutUserUseCase
import com.example.hybridconnect.domain.usecase.PermissionHandlerUseCase
import com.example.hybridconnect.domain.usecase.ReadMpesaMessagesUseCase
import com.example.hybridconnect.domain.usecase.ResendEmailVerificationOtpUseCase
import com.example.hybridconnect.domain.usecase.SubscriptionIdFetcherUseCase
import com.example.hybridconnect.domain.usecase.UpdateAgentUseCase
import com.example.hybridconnect.domain.usecase.ValidateMessageUseCase
import com.example.hybridconnect.domain.usecase.VerifyOtpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideSubscriptionIdFetcherUseCase(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository,
    ): SubscriptionIdFetcherUseCase {
        return SubscriptionIdFetcherUseCase(context, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideGetAppStatusUseCase(settingsRepository: SettingsRepository): GetAppStatusUseCase {
        return GetAppStatusUseCase(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateAgentUseCase(authRepository: AuthRepository): UpdateAgentUseCase {
        return UpdateAgentUseCase(authRepository)
    }

    @Provides
    @Singleton
    @Named("mpesaMessageExtractor")
    fun provideMessageExtractor(): MessageExtractor {
        return DefaultMessageExtractor()
    }

    @Provides
    @Singleton
    @Named("tillMessageExtractor")
    fun provideTillMessageExtractor(): MessageExtractor {
        return TillMessageExtractor()
    }

    @Provides
    @Singleton
    @Named("siteLinkMessageExtractor")
    fun provideSiteLinkMessageExtractor(): MessageExtractor {
        return SiteLinkMessageExtractor()
    }

    @Provides
    @Singleton
    fun provideSmsProcessor(
        validateMessageUseCase: ValidateMessageUseCase,
        extractMessageDetailsUseCase: ExtractMessageDetailsUseCase,
        getOfferByPriceUseCase: GetOfferByPriceUseCase,
        transactionRepository: TransactionRepository,
        forwardMessagesUseCase: ForwardMessagesUseCase,
    ): SmsProcessor {
        return SmsProcessor(
            validateMessageUseCase,
            extractMessageDetailsUseCase,
            getOfferByPriceUseCase,
            transactionRepository,
            forwardMessagesUseCase
        )
    }

    @Provides
    @Singleton
    fun provideLoginUserUseCase(
        authRepository: AuthRepository,
        settingsRepository: SettingsRepository,
    ): LoginUserUseCase {
        return LoginUserUseCase(authRepository, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideVerifyOtpUseCase(
        settingsRepository: SettingsRepository,
        authRepository: AuthRepository,
    ): VerifyOtpUseCase {
        return VerifyOtpUseCase(settingsRepository, authRepository)
    }

    @Provides
    @Singleton
    fun provideResendEmailVerificationOtpUseCase(authRepository: AuthRepository): ResendEmailVerificationOtpUseCase {
        return ResendEmailVerificationOtpUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUserUseCase(
        settingsRepository: SettingsRepository,
        authRepository: AuthRepository,
    ): LogoutUserUseCase {
        return LogoutUserUseCase(settingsRepository, authRepository)
    }

    @Provides
    @Singleton
    fun provideValidateMessageUseCase(settingsRepository: SettingsRepository): ValidateMessageUseCase {
        return ValidateMessageUseCase(settingsRepository)
    }

    @Provides
    @Singleton
    fun provideExtractMessageDetailsUseCase(
        @Named("mpesaMessageExtractor") mpesaMessageExtractor: MessageExtractor,
        @Named("tillMessageExtractor") tillMessagesExtractor: MessageExtractor,
        @Named("siteLinkMessageExtractor") siteLinkMessageExtractor: MessageExtractor,
    ): ExtractMessageDetailsUseCase {
        return ExtractMessageDetailsUseCase(
            mpesaMessageExtractor,
            tillMessagesExtractor,
            siteLinkMessageExtractor
        )
    }

    @Provides
    @Singleton
    fun providePermissionHandlerUseCase(@ApplicationContext context: Context): PermissionHandlerUseCase {
        return PermissionHandlerUseCase(context)
    }

    @Provides
    @Singleton
    fun provideForwardMessagesUseCase(
        @ApplicationContext context: Context,
        transactionRepository: TransactionRepository
    ): ForwardMessagesUseCase {
        return ForwardMessagesUseCase(context, transactionRepository)
    }

    @Provides
    fun provideReadMpesaMessagesUseCase(@ApplicationContext context: Context): ReadMpesaMessagesUseCase {
        return ReadMpesaMessagesUseCase(context)
    }
}