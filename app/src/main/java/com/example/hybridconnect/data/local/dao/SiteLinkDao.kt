package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.SiteLinkEntity

@Dao
interface SiteLinkDao {
    @Insert
    suspend fun insert(siteLink: SiteLinkEntity)

    @Query("SELECT * FROM site_link LIMIT 1")
    suspend fun getSiteLink(): SiteLinkEntity?

    @Update
    suspend fun update(siteLink: SiteLinkEntity)

    @Delete
    suspend fun deleteSiteLink(siteLink: SiteLinkEntity)
}