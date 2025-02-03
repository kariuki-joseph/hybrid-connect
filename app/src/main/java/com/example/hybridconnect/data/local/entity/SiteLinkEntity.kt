package com.example.hybridconnect.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hybridconnect.domain.enums.SiteLinkAccountType

@Entity(
    tableName = "site_link"
)
data class SiteLinkEntity(
    @PrimaryKey val id: String,
    val siteName: String,
    val accountType: SiteLinkAccountType,
    val accountNumber: String,
    val siteLinkURL: String,
    val isActive: Boolean
)