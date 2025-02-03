package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hybridconnect.data.local.entity.PrefEntity

@Dao
interface PrefsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(setting: PrefEntity)

    @Query("SELECT * FROM preferences WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): PrefEntity?

    @Delete
    suspend fun deleteSetting(setting: PrefEntity)
}