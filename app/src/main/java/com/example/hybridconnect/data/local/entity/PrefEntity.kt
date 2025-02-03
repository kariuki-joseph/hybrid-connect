package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "preferences",
    indices = [Index(value = ["key"])]
)
data class PrefEntity(
    @PrimaryKey val key: String,
    val value: String,
)