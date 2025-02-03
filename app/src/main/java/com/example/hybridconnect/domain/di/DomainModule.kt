package com.example.hybridconnect.domain.di

import android.content.Context
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.services.DefaultMessageExtractor
import com.example.hybridconnect.domain.services.SiteLinkMessageExtractor
import com.example.hybridconnect.domain.services.SmsProcessor
import com.example.hybridconnect.domain.services.TillMessageExtractor
import com.example.hybridconnect.domain.services.interfaces.MessageExtractor
import com.example.hybridconnect.domain.usecase.ExtractMessageDetailsUseCase
import com.example.hybridconnect.domain.usecase.GetAppStatusUseCase
import com.example.hybridconnect.domain.usecase.LoginUserUseCase
import com.example.hybridconnect.domain.usecase.LogoutUserUseCase
import com.example.hybridconnect.domain.usecase.PermissionHandlerUseCase
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
        prefsRepository: PrefsRepository,
    ): SubscriptionIdFetcherUseCase {
        return SubscriptionIdFetcherUseCase(context, prefsRepository)
    }

    @Provides
    @Singleton
    fun provideGetAppStatusUseCase(prefsRepository: PrefsRepository): GetAppStatusUseCase {
        return GetAppStatusUseCase(prefsRepository)
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
        extractMessageDetailsUseCase: ExtractMessageDetailsUseCase
    ): SmsProcessor {
        return SmsProcessor(
            validateMessageUseCase,
            extractMessageDetailsUseCase,
        )
    }

    @Provides
    @Singleton
    fun provideLoginUserUseCase(
        authRepository: AuthRepository,
        prefsRepository: PrefsRepository,
    ): LoginUserUseCase {
        return LoginUserUseCase(authRepository, prefsRepository)
    }

    @Provides
    @Singleton
    fun provideVerifyOtpUseCase(
        prefsRepository: PrefsRepository,
        authRepository: AuthRepository,
    ): VerifyOtpUseCase {
        return VerifyOtpUseCase(prefsRepository, authRepository)
    }

    @Provides
    @Singleton
    fun provideResendEmailVerificationOtpUseCase(authRepository: AuthRepository): ResendEmailVerificationOtpUseCase {
        return ResendEmailVerificationOtpUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUserUseCase(
        prefsRepository: PrefsRepository,
        authRepository: AuthRepository,
    ): LogoutUserUseCase {
        return LogoutUserUseCase(prefsRepository, authRepository)
    }

    @Provides
    @Singleton
    fun provideValidateMessageUseCase(prefsRepository: PrefsRepository): ValidateMessageUseCase {
        return ValidateMessageUseCase(prefsRepository)
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
}