package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auto_replies")
data class AutoReplyEntity(
    val title: String,
    @PrimaryKey val type: String,
    val message: String,
    val isActive: Boolean = false,
)
