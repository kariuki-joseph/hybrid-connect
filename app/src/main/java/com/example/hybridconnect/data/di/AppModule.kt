package com.example.hybridconnect.data.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hybridconnect.data.local.dao.AgentDao
import com.example.hybridconnect.data.local.dao.AppOfferDao
import com.example.hybridconnect.data.local.dao.ConnectedAppDao
import com.example.hybridconnect.data.local.dao.OfferDao
import com.example.hybridconnect.data.local.dao.PrefsDao
import com.example.hybridconnect.data.local.dao.TransactionDao
import com.example.hybridconnect.data.local.database.AppDatabase
import com.example.hybridconnect.data.local.entity.PrefEntity
import com.example.hybridconnect.data.local.preferences.SharedPrefsManager
import com.example.hybridconnect.data.remote.api.ApiService
import com.example.hybridconnect.data.repository.AuthRepositoryImpl
import com.example.hybridconnect.data.repository.ConnectedAppRepositoryImpl
import com.example.hybridconnect.data.repository.OfferRepositoryImpl
import com.example.hybridconnect.data.repository.SettingsRepositoryImpl
import com.example.hybridconnect.data.repository.TransactionRepositoryImpl
import com.example.hybridconnect.domain.enums.AppSetting
import com.example.hybridconnect.domain.repository.AuthRepository
import com.example.hybridconnect.domain.repository.ConnectedAppRepository
import com.example.hybridconnect.domain.repository.OfferRepository
import com.example.hybridconnect.domain.repository.SettingsRepository
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

                        prefsDao.set(PrefEntity(AppSetting.RECEIVE_PAYMENTS_VIA_SIM_1.name, "true"))
                        prefsDao.set(PrefEntity(AppSetting.RECEIVE_PAYMENTS_VIA_SIM_2.name, "true"))
                        prefsDao.set(PrefEntity(AppSetting.PROCESS_TILL_MESSAGES.name, "true"))
                        prefsDao.set(PrefEntity(AppSetting.PROCESS_MPESA_MESSAGES.name, "true"))
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
    fun provideConnectedAppDao(db: AppDatabase): ConnectedAppDao {
        return db.connectedAppDao()
    }

    @Provides
    fun provideSettingDao(db: AppDatabase): PrefsDao {
        return db.prefDao()
    }

    @Provides
    fun provideAgentDao(db: AppDatabase): AgentDao {
        return db.agentDao()
    }

    @Provides
    fun provideOfferDao(db: AppDatabase): OfferDao {
        return db.offerDao()
    }

    @Provides
    fun provideAppOfferDao(db: AppDatabase): AppOfferDao {
        return db.appOfferDao()
    }

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao {
        return db.transactionDao()
    }

    @Provides
    @Singleton
    fun providePrefsRepository(
        sharedPrefsManager: SharedPrefsManager,
        prefsDao: PrefsDao,
    ): SettingsRepository {
        return SettingsRepositoryImpl(sharedPrefsManager, prefsDao)
    }


    @Provides
    @Singleton
    fun provideAuthRepository(
        agentDao: AgentDao,
        settingsRepository: SettingsRepository,
        apiService: ApiService,
    ): AuthRepository {
        return AuthRepositoryImpl(agentDao, settingsRepository, apiService)
    }


    @Provides
    @Singleton
    fun provideConnectedAppRepository(
        connectedAppDao: ConnectedAppDao,
        appOfferDao: AppOfferDao,
        settingsRepository: SettingsRepository,
        apiService: ApiService,
    ): ConnectedAppRepository {
        return ConnectedAppRepositoryImpl(
            connectedAppDao = connectedAppDao,
            appOfferDao = appOfferDao,
            settingsRepository = settingsRepository,
            apiService = apiService,
        )
    }

    @Provides
    @Singleton
    fun provideOfferRepository(
        offerDao: OfferDao,
    ): OfferRepository {
        return OfferRepositoryImpl(offerDao)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        offerRepository: OfferRepository,
    ): TransactionRepository {
        return TransactionRepositoryImpl(transactionDao, offerRepository)
    }
}