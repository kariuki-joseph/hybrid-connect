package com.example.hybridconnect.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.hybridconnect.domain.utils.Constants

class SharedPrefsManager(context: Context) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.KEY_PREFS_NAME, Context.MODE_PRIVATE)

     inline fun <reified T> set(key: String, value: T) {
    with(sharedPreferences.edit()) {
        when (T::class) {
            Boolean::class -> putBoolean(key, value as Boolean)
            Int::class -> putInt(key, value as Int)
            Float::class -> putFloat(key, value as Float)
            Long::class -> putLong(key, value as Long)
            String::class -> putString(key, value as String)
            else -> throw IllegalArgumentException("Unsupported type")
            }
        apply()
        }
    }

    inline fun <reified T> get(key: String, defaultValue: T): T {
        return when (T::class) {
            Boolean::class -> sharedPreferences.getBoolean(key, defaultValue as Boolean) as T
            Int::class -> sharedPreferences.getInt(key, defaultValue as Int) as T
            Float::class -> sharedPreferences.getFloat(key, defaultValue as Float) as T
            Long::class -> sharedPreferences.getLong(key, defaultValue as Long) as T
            String::class -> sharedPreferences.getString(key, defaultValue as String) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }
}