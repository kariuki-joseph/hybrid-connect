package com.example.hybridconnect.data.repository

import android.util.Log
import com.example.hybridconnect.data.local.dao.AutoReplyDao
import com.example.hybridconnect.data.mappers.toDomain
import com.example.hybridconnect.data.mappers.toEntity
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.domain.model.AutoReply
import com.example.hybridconnect.domain.repository.AutoReplyRepository
import javax.inject.Inject

private const val TAG = "AutoReplyRepositoryImpl"
class AutoReplyRepositoryImpl @Inject constructor(
    private val autoReplyDao: AutoReplyDao
): AutoReplyRepository {
    override suspend fun getAutoReplies(): List<AutoReply> {
        try {
            return autoReplyDao.getAutoReplies().map { it.toDomain() }
        } catch (e: Exception){
            Log.e(TAG, "getAutoReplies: ", e)
            throw e
        }

    }

    override suspend fun getAutoReplyByType(autoReplyType: AutoReplyType): AutoReply {
        try {
            return autoReplyDao.getByType(autoReplyType.name).toDomain()
        } catch (e: Exception){
            Log.d(TAG, "getAutoReply", e)
            throw e
        }
    }

    override suspend fun updateAutoReply(autoReply: AutoReply) {
        try {
            autoReplyDao.updateAutoReply(autoReply.toEntity())
        } catch (e: Exception){
            Log.e(TAG, "updateAutoReply: ", e)
            throw e
        }
    }

    override suspend fun activateAutoReply(autoReplyType: AutoReplyType) {
        try {
            autoReplyDao.activateAutoReplies(autoReplyType.name)
        } catch (e: Exception){
            Log.e(TAG, "activateAutoReply: ", e)
            throw e
        }
    }

    override suspend fun deactivateAutoReply(autoReplyType: AutoReplyType) {
        try {
            autoReplyDao.deactivateAutoReplies(autoReplyType.name)
        } catch (e: Exception){
            Log.e(TAG, "deactivateAutoReply: ", e)
            throw e
        }
    }
}