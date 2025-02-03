package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.AutoReplyEntity
import com.example.hybridconnect.domain.enums.AutoReplyType

@Dao
interface AutoReplyDao {
    @Insert
    suspend fun insertAutoReply(autoReply: AutoReplyEntity)

    @Update
    suspend fun updateAutoReply(autoReply: AutoReplyEntity)

    @Query("SELECT * FROM auto_replies")
    suspend fun getAutoReplies(): List<AutoReplyEntity>

    @Query("SELECT * FROM auto_replies WHERE type = :autoReplyType")
    suspend fun getByType(autoReplyType: String): AutoReplyEntity

    @Query("UPDATE auto_replies SET isActive = 1 WHERE type = :type")
    suspend fun activateAutoReplies(type: String)

    @Query("UPDATE auto_replies SET isActive = 0 WHERE type = :type")
    suspend fun deactivateAutoReplies(type: String)
}