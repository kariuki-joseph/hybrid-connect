package com.example.hybridconnect.domain.utils

object Constants {
    const val APP_NAME = "Hybrid Connect"
    const val DATABASE_NAME = "app_database"
    const val BASE_URL = "https://test.api.hybridconnect.com"
    // ussd worker params
    const val KEY_USSD = "ussd"
    const val KEY_SUBSCRIPTION_ID = "sid"
    const val KEY_TRANSACTION_ID = "transaction_id"
    const val KEY_MAX_USSD_RETRIES = "max_ussd_retries"
    const val KEY_FORCE_DIAL = "is_manual_retry"
    // Preferences
    const val KEY_PREFS_NAME = "bingwa_prefs"
    // user
    const val KEY_ACCESS_TOKEN = "access_token"
    // settings
    const val IS_APP_ACTIVE = "is_app_active"
    // admin
    const val KEY_ADMIN_PHONE = "admin_phone"
    // airtime (BH = Admin phone)
    const val AIRTIME_SUBSCRIPTION_USSD_CODE = "*140*AMT*BH#"
}