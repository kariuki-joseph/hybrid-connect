package com.example.hybridconnect.data.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hybridconnect.data.local.dao.AgentCommissionDao
import com.example.hybridconnect.data.local.dao.AgentDao
import com.example.hybridconnect.data.local.dao.AutoReplyDao
import com.example.hybridconnect.data.local.dao.CommissionRateDao
import com.example.hybridconnect.data.local.dao.CustomerDao
import com.example.hybridconnect.data.local.dao.OfferDao
import com.example.hybridconnect.data.local.dao.PrefsDao
import com.example.hybridconnect.data.local.dao.SiteLinkDao
import com.example.hybridconnect.data.local.dao.SubscriptionPackageDao
import com.example.hybridconnect.data.local.dao.SubscriptionPlanDao
import com.example.hybridconnect.data.local.dao.TransactionDao
import com.example.hybridconnect.data.local.database.AppDatabase
import com.example.hybridconnect.data.local.entity.PrefEntity
import com.example.hybridconnect.data.local.preferences.SharedPrefsManager
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.data.repository.AgentCommissionRepositoryImpl
import com.example.hybridconnect.data.repository.AuthRepositoryImpl
import com.example.hybridconnect.data.repository.AutoReplyRepositoryImpl
import com.example.hybridconnect.data.repository.CommissionRateRepositoryImpl
import com.example.hybridconnect.data.repository.CustomerRepositoryImpl
import com.example.hybridconnect.data.repository.OfferRepositoryImpl
import com.example.hybridconnect.data.repository.PrefsRepositoryImpl
import com.example.hybridconnect.data.repository.SiteLinkRepositoryImpl
import com.example.hybridconnect.data.repository.SubscriptionPackageRepositoryImpl
import com.example.hybridconnect.data.repository.SubscriptionPlanRepositoryImpl
import com.example.hybridconnect.data.repository.TransactionRepositoryImpl
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.domain.enums.OfferTag
import com.example.hybridconnect.domain.enums.OfferType
import com.example.hybridconnect.domain.model.AutoReply
import com.example.hybridconnect.domain.model.Offer
import com.example.hybridconnect.domain.repository.AgentCommissionRepository
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.AutoReplyRepository
import com.example.hybridconnect.domain.repository.CommissionRateRepository
import com.example.hybridconnect.domain.repository.CustomerRepository
import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.repository.PrefsRepository
import com.example.hybridconnect.domain.repository.SiteLinkRepository
import com.example.hybridconnect.domain.repository.SubscriptionPackageRepository
import com.example.hybridconnect.domain.repository.SubscriptionPlanRepository
import com.example.hybridconnect.domain.repository.TransactionRepository
import com.example.hybridconnect.domain.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration().addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = Room.databaseBuilder(
                            app, AppDatabase::class.java, Constants.DATABASE_NAME
                        ).build()
                        val prefsDao = database.prefDao()

                        val dbInitialized =
                            prefsDao.getSetting(AppSetting.DB_INITIALIZED.name)?.value.toBoolean()
                        if (dbInitialized) return@launch

                        val offerDao = database.offerDao()
                        val autoReplyDao = database.autoReplyDao()

                        prefsDao.set(PrefEntity(AppSetting.RECEIVE_PAYMENTS_VIA_SIM_1.name, "true"))
                        prefsDao.set(PrefEntity(AppSetting.RECEIVE_PAYMENTS_VIA_SIM_2.name, "true"))
                        prefsDao.set(PrefEntity(AppSetting.DIAL_USSD_VIA_SIM_1.name, "true"))
                        prefsDao.set(PrefEntity(AppSetting.PROCESS_TILL_MESSAGES.name, "true"))
                        prefsDao.set(PrefEntity(AppSetting.PROCESS_MPESA_MESSAGES.name, "true"))

                        // create default offers
                        val defaultOffers = listOf(
                            Offer(
                                id = UUID.randomUUID(),
                                name = "1.5Gb valid 3Hrs",
                                ussdCode = "*180*5*2*BH*1*1#",
                                price = 50,
                                type = OfferType.DATA,
                                tag = OfferTag.OFFER_1,
                            ), Offer(
                                id = UUID.randomUUID(),
                                name = "350Mbs valid 7 Days",
                                ussdCode = "*180*5*2*BH*2*1#",
                                price = 49,
                                type = OfferType.DATA,
                                tag = OfferTag.OFFER_2,
                            ), Offer(
                                id = UUID.randomUUID(),
                                name = " 2.5Gb valid 7 Days",
                                ussdCode = "*180*5*2*BH*3*1#",
                                price = 300,
                                type = OfferType.DATA,
                                tag = OfferTag.OFFER_3,
                            ), Offer(
                                id = UUID.randomUUID(),
                                name = "6Gb valid 7 Days",
                                ussdCode = "*180*5*2*BH*4*1#",
                                price = 700,
                                type = OfferType.DATA,
                                tag = OfferTag.OFFER_4,
                            ),
                            Offer(
                                id = UUID.randomUUID(),
                                name = "1Gb valid 1Hr",
                                ussdCode = "*180*5*2*BH*5*1#",
                                price = 19,
                                type = OfferType.DATA,
                                tag = OfferTag.OFFER_5,
                            ),
                            Offer(
                                id = UUID.randomUUID(),
                                name = "250Mbs valid 24Hrs",
                                ussdCode = "*180*5*2*BH*6*1#",
                                price = 20,
                                type = OfferType.DATA,
                                tag = OfferTag.OFFER_6,
                            ),
                            Offer(
                                id = UUID.randomUUID(),
                                name = "1Gb valid 24Hrs",
                                ussdCode = "*180*5*2*BH*7*1#",
                                price = 99,
                                type = OfferType.DATA,
                                tag = OfferTag.OFFER_7,
                            ),
                            Offer(
                                id = UUID.randomUUID(),
                                name = "1.25Gb valid till Midnight",
                                ussdCode = "*180*5*2*BH*8*1#",
                                price = 55,
                                type = OfferType.DATA,
                                tag = OfferTag.OFFER_8,
                            )
                        )
                        defaultOffers.forEach { offer ->
                            offerDao.insert(offer.toEntity())
                        }

                        val defaultAutoReplies = listOf(
                            AutoReply(
                                title = "Successful Request AutoReply",
                                type = AutoReplyType.SUCCESS,
                                message = "Hi <firstName>, Thank you for purchasing from Hybrid Connect",
                                isActive = false
                            ),
                            AutoReply(
                                title = "Offer Already Recommended AutoReply",
                                type = AutoReplyType.ALREADY_RECOMMENDED,
                                message = "Hello <firstName>, you have already purchased this offer today. Please try again tomorrow",
                                isActive = false
                            ),
                            AutoReply(
                                title = "Failed Request AutoReply",
                                type = AutoReplyType.FAILED,
                                message = "Hello <firstName>, Your request failed. Please hold as we look into the issue",
                                isActive = false
                            ),
                            AutoReply(
                                title = "Unavailable Offer AutoReply",
                                type = AutoReplyType.OFFER_UNAVAILABLE,
                                message = "Hi <firstName>, there is no offer matching the amount you have paid. Please pay the correct amount then try again",
                                isActive = false
                            ),
                        )

                        defaultAutoReplies.forEach { reply ->
                            autoReplyDao.insertAutoReply(reply.toEntity())
                        }

                        prefsDao.set(PrefEntity(AppSetting.DB_INITIALIZED.name, "true"))
                    }
                }
            }).build()

    }

    @Provides
    @Singleton
    fun provideSharedPrefsManager(@ApplicationContext context: Context): SharedPrefsManager {
        return SharedPrefsManager(context)
    }

    // DAOs
    @Provides
    fun provideOfferDao(db: AppDatabase): OfferDao {
        return db.offerDao()
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao {
        return db.transactionDao()
    }

    @Provides
    fun provideCustomerDao(db: AppDatabase): CustomerDao {
        return db.customerDao()
    }

    @Provides
    fun provideSettingDao(db: AppDatabase): PrefsDao {
        return db.prefDao()
    }

    @Provides
    fun provideSubscriptionDao(db: AppDatabase): SubscriptionPackageDao {
        return db.subscriptionPackageDao()
    }

    @Provides
    fun provideAgentDao(db: AppDatabase): AgentDao {
        return db.agentDao()
    }

    @Provides
    fun provideCommissionRateDao(db: AppDatabase): CommissionRateDao {
        return db.getCommissionRateDao()
    }

    @Provides
    fun provideAgentCommissionDao(db: AppDatabase): AgentCommissionDao {
        return db.agentCommissionDao()
    }

    @Provides
    fun provideSiteLinkDao(db: AppDatabase): SiteLinkDao {
        return db.siteLinkDao()
    }

    @Provides
    fun provideAutoReplyDao(db: AppDatabase): AutoReplyDao {
        return db.autoReplyDao()
    }

    @Provides
    fun provideSubscriptionPlanDao(db: AppDatabase): SubscriptionPlanDao {
        return db.subscriptionPlanDao()
    }


    // Repositories
    @Provides
    @Singleton
    fun provideOfferRepository(db: AppDatabase): OfferRepository {
        return OfferRepositoryImpl(db.offerDao())
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        db: AppDatabase,
        customerRepository: CustomerRepository,
        offerRepository: OfferRepository,
    ): TransactionRepository {
        return TransactionRepositoryImpl(
            db.transactionDao(), customerRepository, offerRepository
        )
    }

    @Provides
    @Singleton
    fun provideCustomerRepository(db: AppDatabase): CustomerRepository {
        return CustomerRepositoryImpl(db.customerDao())
    }

    @Provides
    @Singleton
    fun providePrefsRepository(
        sharedPrefsManager: SharedPrefsManager,
        prefsDao: PrefsDao,
    ): PrefsRepository {
        return PrefsRepositoryImpl(sharedPrefsManager, prefsDao)
    }

    @Provides
    @Singleton
    fun provideSubscriptionRepository(
        subscriptionPackageDao: SubscriptionPackageDao,
        apiService: ApiService,
        prefsDao: PrefsDao,
    ): SubscriptionPackageRepository {
        return SubscriptionPackageRepositoryImpl(subscriptionPackageDao, apiService, prefsDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        agentDao: AgentDao,
        prefsRepository: PrefsRepository,
        apiService: ApiService,
    ): AuthRepository {
        return AuthRepositoryImpl(agentDao, prefsRepository, apiService)
    }

    @Provides
    @Singleton
    fun provideCommissionRateRepository(
        commissionRateDao: CommissionRateDao,
        apiService: ApiService,
    ): CommissionRateRepository {
        return CommissionRateRepositoryImpl(commissionRateDao, apiService)
    }

    @Provides
    @Singleton
    fun provideSiteLinkRepository(
        siteLinkDao: SiteLinkDao,
        apiService: ApiService,
    ): SiteLinkRepository {
        return SiteLinkRepositoryImpl(siteLinkDao, apiService)
    }

    @Provides
    @Singleton
    fun provideAgentCommissionRepository(
        agentCommissionDao: AgentCommissionDao,
    ): AgentCommissionRepository {
        return AgentCommissionRepositoryImpl(agentCommissionDao)
    }

    @Provides
    @Singleton
    fun provideAutoReplyRepository(autoReplyDao: AutoReplyDao): AutoReplyRepository {
        return AutoReplyRepositoryImpl(autoReplyDao)
    }

    @Provides
    @Singleton
    fun provideSubscriptionPlanRepository(
        apiService: ApiService,
        subscriptionPlanDao: SubscriptionPlanDao,
    ): SubscriptionPlanRepository {
        return SubscriptionPlanRepositoryImpl(apiService, subscriptionPlanDao)
    }
}