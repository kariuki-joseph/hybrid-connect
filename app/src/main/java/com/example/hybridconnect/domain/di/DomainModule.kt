package com.example.hybridconnect.domain.di

import android.content.Context
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.data.repository.PaymentRepositoryImpl
import com.example.hybridconnect.domain.repository.AgentCommissionRepository
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.AutoReplyRepository
import com.example.hybridconnect.domain.repository.CommissionRateRepository
import com.example.hybridconnect.domain.repository.CustomerRepository
import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.repository.PaymentRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import com.example.hybridconnect.domain.repository.SubscriptionPackageRepository
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.services.DefaultMessageExtractor
import com.example.hybridconnect.domain.services.SiteLinkMessageExtractor
import com.example.hybridconnect.domain.services.SmsProcessor
import com.example.hybridconnect.domain.services.SocketService
import com.example.hybridconnect.domain.services.TillMessageExtractor
import com.example.hybridconnect.domain.services.interfaces.MessageExtractor
import com.example.hybridconnect.domain.usecase.ActivateAppUseCase
import com.example.hybridconnect.domain.usecase.ActivateSiteLinkUseCase
import com.example.hybridconnect.domain.usecase.AddOfferUseCase
import com.example.hybridconnect.domain.usecase.AddSiteLinkOfferUseCase
import com.example.hybridconnect.domain.usecase.AddSubscriptionUseCase
import com.example.hybridconnect.domain.usecase.CreateSmsTransactionUseCase
import com.example.hybridconnect.domain.usecase.CreateTransactionUseCase
import com.example.hybridconnect.domain.usecase.DeactivateAppUseCase
import com.example.hybridconnect.domain.usecase.DecrementCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.DeleteSiteLinkOfferUseCase
import com.example.hybridconnect.domain.usecase.DeleteSiteLinkUseCase
import com.example.hybridconnect.domain.usecase.DeleteTransactionUseCase
import com.example.hybridconnect.domain.usecase.DialUssdUseCase
import com.example.hybridconnect.domain.usecase.ExtractMessageDetailsUseCase
import com.example.hybridconnect.domain.usecase.FormatUssdUseCase
import com.example.hybridconnect.domain.usecase.GetAppStatusUseCase
import com.example.hybridconnect.domain.usecase.GetAutoRepliesUseCase
import com.example.hybridconnect.domain.usecase.GetCommissionForDatesUseCase
import com.example.hybridconnect.domain.usecase.GetCommissionUseCase
import com.example.hybridconnect.domain.usecase.GetCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.GetCustomersUseCase
import com.example.hybridconnect.domain.usecase.GetLastTransactionForCustomerUseCase
import com.example.hybridconnect.domain.usecase.GetOfferByPriceUseCase
import com.example.hybridconnect.domain.usecase.GetOffersUseCase
import com.example.hybridconnect.domain.usecase.GetOrCreateCustomerUseCase
import com.example.hybridconnect.domain.usecase.GetScheduledTransactionsUseCase
import com.example.hybridconnect.domain.usecase.GetSiteLinkUseCase
import com.example.hybridconnect.domain.usecase.GetSubscriptionPlansUseCase
import com.example.hybridconnect.domain.usecase.GetTransactionUseCase
import com.example.hybridconnect.domain.usecase.GetTransactionsForCustomerUseCase
import com.example.hybridconnect.domain.usecase.GetTransactionsForPeriodUseCase
import com.example.hybridconnect.domain.usecase.IncrementAgentCommissionUseCase
import com.example.hybridconnect.domain.usecase.IncrementCustomerBalanceUseCase
import com.example.hybridconnect.domain.usecase.LoginUserUseCase
import com.example.hybridconnect.domain.usecase.LogoutUserUseCase
import com.example.hybridconnect.domain.usecase.ObserveOffersUseCase
import com.example.hybridconnect.domain.usecase.ObserveTransactionsUseCase
import com.example.hybridconnect.domain.usecase.PermissionHandlerUseCase
import com.example.hybridconnect.domain.usecase.RefreshCommissionRatesUseCase
import com.example.hybridconnect.domain.usecase.RegisterUserUseCase
import com.example.hybridconnect.domain.usecase.RequestSiteLinkUseCase
import com.example.hybridconnect.domain.usecase.RescheduleTransactionUseCase
import com.example.hybridconnect.domain.usecase.ResendEmailVerificationOtpUseCase
import com.example.hybridconnect.domain.usecase.RetryTransactionUseCase
import com.example.hybridconnect.domain.usecase.SendAutoReplyMessageUseCase
import com.example.hybridconnect.domain.usecase.SendSmsUseCase
import com.example.hybridconnect.domain.usecase.SubscriptionIdFetcherUseCase
import com.example.hybridconnect.domain.usecase.UpdateAgentUseCase
import com.example.hybridconnect.domain.usecase.UpdateSiteLinkUseCase
import com.example.hybridconnect.domain.usecase.UpdateTransactionResponseUseCase
import com.example.hybridconnect.domain.usecase.UpdateTransactionStatusUseCase
import com.example.hybridconnect.domain.usecase.ValidateMessageUseCase
import com.example.hybridconnect.domain.usecase.VerifyOtpUseCase
import com.example.hybridconnect.domain.usecase.customer.DeleteCustomerUseCase
import com.example.hybridconnect.domain.usecase.customer.UpdateCustomerInfoUseCase
import com.example.hybridconnect.domain.usecase.socket.ObserveWebsocketMessages
import com.example.hybridconnect.domain.usecase.subscriptions.GetSubscriptionPackageUseCase
import com.example.hybridconnect.domain.usecase.subscriptions.GetSubscriptionPackagesUseCase
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
        getOrCreateCustomerUseCase: GetOrCreateCustomerUseCase,
        extractMessageDetailsUseCase: ExtractMessageDetailsUseCase,
        getOfferByPriceUseCase: GetOfferByPriceUseCase,
        createTransactionUseCase: CreateSmsTransactionUseCase,
        dialUssdUseCase: DialUssdUseCase,
        incrementCustomerBalanceUseCase: IncrementCustomerBalanceUseCase,
        prefsRepository: PrefsRepository,
        sendAutoReplyMessageUseCase: SendAutoReplyMessageUseCase,
        getLastTransactionForCustomerUseCase: GetLastTransactionForCustomerUseCase,
    ): SmsProcessor {
        return SmsProcessor(
            validateMessageUseCase,
            getOrCreateCustomerUseCase,
            extractMessageDetailsUseCase,
            getOfferByPriceUseCase,
            createTransactionUseCase,
            dialUssdUseCase,
            incrementCustomerBalanceUseCase,
            prefsRepository,
            sendAutoReplyMessageUseCase,
            getLastTransactionForCustomerUseCase,
        )
    }

    @Provides
    @Singleton
    fun providePaymentRepository(
        apiService: ApiService,
        prefsRepository: PrefsRepository,
    ): PaymentRepository {
        return PaymentRepositoryImpl(apiService, prefsRepository)
    }

    @Provides
    @Singleton
    fun provideAddOfferUseCase(offerRepository: OfferRepository): AddOfferUseCase {
        return AddOfferUseCase(offerRepository)
    }

    @Provides
    @Singleton
    fun provideObserveOffersUseCase(offerRepository: OfferRepository): ObserveOffersUseCase {
        return ObserveOffersUseCase(offerRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUserUseCase(authRepository: AuthRepository): RegisterUserUseCase {
        return RegisterUserUseCase(authRepository)
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
        siteLinkRepository: SiteLinkRepository,
    ): LogoutUserUseCase {
        return LogoutUserUseCase(prefsRepository, authRepository, siteLinkRepository)
    }

    @Provides
    @Singleton
    fun provideObserveTransactionsUseCase(
        transactionRepository: TransactionRepository,
    ): ObserveTransactionsUseCase {
        return ObserveTransactionsUseCase(transactionRepository)
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
    fun provideGetOrCreateCustomerUseCase(customerRepository: CustomerRepository): GetOrCreateCustomerUseCase {
        return GetOrCreateCustomerUseCase(customerRepository)
    }

    @Provides
    @Singleton
    fun provideCreateSmsTransactionUseCase(
        transactionRepository: TransactionRepository,
        customerRepository: CustomerRepository,
    ): CreateSmsTransactionUseCase {
        return CreateSmsTransactionUseCase(transactionRepository, customerRepository)
    }

    @Provides
    @Singleton
    fun provideDialUssdUseCase(
        @ApplicationContext context: Context,
        permissionHandler: PermissionHandlerUseCase,
        transactionRepository: TransactionRepository,
        subscriptionPlanRepository: SubscriptionPlanRepository,
        updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
        updateTransactionResponseUseCase: UpdateTransactionResponseUseCase,
    ): DialUssdUseCase {
        return DialUssdUseCase(
            context,
            permissionHandler,
            transactionRepository,
            subscriptionPlanRepository,
            updateTransactionStatusUseCase,
            updateTransactionResponseUseCase
        )
    }

    @Provides
    @Singleton
    fun provideGetOfferByPriceUseCase(offerRepository: OfferRepository): GetOfferByPriceUseCase {
        return GetOfferByPriceUseCase(offerRepository)
    }


    @Provides
    @Singleton
    fun providePermissionHandlerUseCase(@ApplicationContext context: Context): PermissionHandlerUseCase {
        return PermissionHandlerUseCase(context)
    }

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
    fun provideFormatUssdUseCase(): FormatUssdUseCase {
        return FormatUssdUseCase()
    }

    @Provides
    @Singleton
    fun provideCreateTransactionUseCase(
        transactionRepository: TransactionRepository,
        customerRepository: CustomerRepository,
    ): CreateTransactionUseCase {
        return CreateTransactionUseCase(
            transactionRepository,
            customerRepository,
        )
    }

    @Provides
    @Singleton
    fun provideDecrementCustomerBalanceUseCase(customerRepository: CustomerRepository): DecrementCustomerBalanceUseCase {
        return DecrementCustomerBalanceUseCase(customerRepository)
    }

    @Provides
    @Singleton
    fun provideIncrementCustomerBalanceUseCase(customerRepository: CustomerRepository): IncrementCustomerBalanceUseCase {
        return IncrementCustomerBalanceUseCase(customerRepository)
    }

    @Provides
    @Singleton
    fun provideGetCustomerBalanceUseCase(customerRepository: CustomerRepository): GetCustomerBalanceUseCase {
        return GetCustomerBalanceUseCase(customerRepository)
    }

    @Provides
    @Singleton
    fun provideGetCommissionUseCase(commissionRateRepository: CommissionRateRepository): GetCommissionUseCase {
        return GetCommissionUseCase(commissionRateRepository)
    }

    @Provides
    @Singleton
    fun provideRefreshCommissionRatesUseCase(commissionRateRepository: CommissionRateRepository): RefreshCommissionRatesUseCase {
        return RefreshCommissionRatesUseCase(commissionRateRepository)
    }

    @Provides
    @Singleton
    fun provideGetCommissionTodayUseCase(agentCommissionRateRepository: AgentCommissionRepository): GetCommissionForDatesUseCase {
        return GetCommissionForDatesUseCase(agentCommissionRateRepository)
    }

    @Provides
    @Singleton
    fun provideRequestSiteLinkUseCase(siteLinkRepository: SiteLinkRepository): RequestSiteLinkUseCase {
        return RequestSiteLinkUseCase(siteLinkRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateSiteLinkUseCase(siteLinkRepository: SiteLinkRepository): UpdateSiteLinkUseCase {
        return UpdateSiteLinkUseCase(siteLinkRepository)
    }

    @Provides
    @Singleton
    fun provideGetSiteLinkUseCase(siteLinkRepository: SiteLinkRepository): GetSiteLinkUseCase {
        return GetSiteLinkUseCase(siteLinkRepository)
    }

    @Provides
    @Singleton
    fun provideGetTransactionsForPeriodUseCase(transactionRepository: TransactionRepository): GetTransactionsForPeriodUseCase {
        return GetTransactionsForPeriodUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideGetTransactionsForCustomerUseCase(transactionRepository: TransactionRepository): GetTransactionsForCustomerUseCase {
        return GetTransactionsForCustomerUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideActivateAppUseCase(prefsRepository: PrefsRepository): ActivateAppUseCase {
        return ActivateAppUseCase(prefsRepository)
    }

    @Provides
    @Singleton
    fun provideDeactivateAppUseCase(prefsRepository: PrefsRepository): DeactivateAppUseCase {
        return DeactivateAppUseCase(prefsRepository)
    }

    @Provides
    @Singleton
    fun provideGetAppStatusUseCase(prefsRepository: PrefsRepository): GetAppStatusUseCase {
        return GetAppStatusUseCase(prefsRepository)
    }

    @Provides
    @Singleton
    fun provideGetCustomersUseCase(customerRepository: CustomerRepository): GetCustomersUseCase {
        return GetCustomersUseCase(customerRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteTransactionUseCase(
        transactionRepository: TransactionRepository,
        decrementCustomerBalanceUseCase: DecrementCustomerBalanceUseCase,
    ): DeleteTransactionUseCase {
        return DeleteTransactionUseCase(transactionRepository, decrementCustomerBalanceUseCase)
    }

    @Provides
    @Singleton
    fun provideGetTransactionUseCase(transactionRepository: TransactionRepository): GetTransactionUseCase {
        return GetTransactionUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateTransactionStatusUseCase(transactionRepository: TransactionRepository): UpdateTransactionStatusUseCase {
        return UpdateTransactionStatusUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateTransactionResponseUseCase(transactionRepository: TransactionRepository): UpdateTransactionResponseUseCase {
        return UpdateTransactionResponseUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideGetScheduledTransactionsUseCase(transactionRepository: TransactionRepository): GetScheduledTransactionsUseCase {
        return GetScheduledTransactionsUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideGetOffersUseCase(offerRepository: OfferRepository): GetOffersUseCase {
        return GetOffersUseCase(offerRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateAgentUseCase(authRepository: AuthRepository): UpdateAgentUseCase {
        return UpdateAgentUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideAddSiteLinkOfferUseCase(
        siteLinkRepository: SiteLinkRepository,
        offerRepository: OfferRepository,
    ): AddSiteLinkOfferUseCase {
        return AddSiteLinkOfferUseCase(siteLinkRepository, offerRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteSiteLinkOfferUseCase(
        siteLinkRepository: SiteLinkRepository,
        offerRepository: OfferRepository,
    ): DeleteSiteLinkOfferUseCase {
        return DeleteSiteLinkOfferUseCase(siteLinkRepository, offerRepository)
    }

    @Provides
    @Singleton
    fun provideGetSubscriptionPackagesUseCase(subscriptionPackageRepository: SubscriptionPackageRepository): GetSubscriptionPackagesUseCase {
        return GetSubscriptionPackagesUseCase(subscriptionPackageRepository)
    }

    @Provides
    @Singleton
    fun provideGetSubscriptionPlansUseCase(subscriptionPlanRepository: SubscriptionPlanRepository): GetSubscriptionPlansUseCase {
        return GetSubscriptionPlansUseCase(subscriptionPlanRepository)
    }

    @Provides
    @Singleton
    fun provideGetSubscriptionUseCase(subscriptionPackageRepository: SubscriptionPackageRepository): GetSubscriptionPackageUseCase {
        return GetSubscriptionPackageUseCase(subscriptionPackageRepository)
    }

    @Provides
    @Singleton
    fun provideObserveMessagesUseCase(socketService: SocketService): ObserveWebsocketMessages {
        return ObserveWebsocketMessages(socketService)
    }

    @Provides
    @Singleton
    fun provideAddSubscriptionUseCase(
        subscriptionPlanRepository: SubscriptionPlanRepository,
        @ApplicationContext context: Context,
    ): AddSubscriptionUseCase {
        return AddSubscriptionUseCase(subscriptionPlanRepository, context)
    }

    @Provides
    @Singleton
    fun provideUpdateCustomerInfoUseCase(customerRepository: CustomerRepository): UpdateCustomerInfoUseCase {
        return UpdateCustomerInfoUseCase(customerRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteCustomerUseCase(
        customerRepository: CustomerRepository,
        transactionRepository: TransactionRepository,
    ): DeleteCustomerUseCase {
        return DeleteCustomerUseCase(customerRepository, transactionRepository)
    }

    @Provides
    @Singleton
    fun provideActivateSiteLinkUseCase(
        prefsRepository: PrefsRepository,
        siteLinkRepository: SiteLinkRepository,
    ): ActivateSiteLinkUseCase {
        return ActivateSiteLinkUseCase(prefsRepository, siteLinkRepository)
    }

    @Provides
    @Singleton
    fun provideIncrementAgentCommissionUseCase(
        agentCommissionRepository: AgentCommissionRepository,
    ): IncrementAgentCommissionUseCase {
        return IncrementAgentCommissionUseCase(agentCommissionRepository)
    }

    @Provides
    @Singleton
    fun provideSendSmsUseCase(
        @ApplicationContext context: Context,
    ): SendSmsUseCase {
        return SendSmsUseCase(context)
    }


    @Provides
    @Singleton
    fun provideSendAutoReplyMessageUseCase(
        autoReplyRepository: AutoReplyRepository,
        sendSmsUseCase: SendSmsUseCase,
    ): SendAutoReplyMessageUseCase {
        return SendAutoReplyMessageUseCase(autoReplyRepository, sendSmsUseCase)
    }

    @Provides
    @Singleton
    fun provideGetAutoRepliesUseCase(
        autoReplyRepository: AutoReplyRepository,
    ): GetAutoRepliesUseCase {
        return GetAutoRepliesUseCase(autoReplyRepository)
    }

    @Provides
    @Singleton
    fun provideGetLastTransactionForCustomerUseCase(transactionRepository: TransactionRepository): GetLastTransactionForCustomerUseCase {
        return GetLastTransactionForCustomerUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideRetryTransactionUseCase(
        updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
        decrementCustomerBalanceUseCase: DecrementCustomerBalanceUseCase,
        ialUssdUseCase: DialUssdUseCase,
    ): RetryTransactionUseCase {
        return RetryTransactionUseCase(
            updateTransactionStatusUseCase,
            decrementCustomerBalanceUseCase,
            ialUssdUseCase
        )
    }

    @Provides
    @Singleton
    fun provideRescheduleTransactionUseCase(
        createTransactionUseCase: CreateTransactionUseCase,
        updateTransactionStatusUseCase: UpdateTransactionStatusUseCase,
        deleteTransactionUseCase: DeleteTransactionUseCase,
        dialUssdUseCase: DialUssdUseCase,
    ): RescheduleTransactionUseCase {
        return RescheduleTransactionUseCase(
            createTransactionUseCase,
            updateTransactionStatusUseCase,
            deleteTransactionUseCase,
            dialUssdUseCase,
        )
    }

    @Provides
    @Singleton
    fun provideDeleteSiteLinkUseCase(
        siteLinkRepository: SiteLinkRepository,
        offerRepository: OfferRepository,
    ): DeleteSiteLinkUseCase {
        return DeleteSiteLinkUseCase(siteLinkRepository, offerRepository)
    }
}