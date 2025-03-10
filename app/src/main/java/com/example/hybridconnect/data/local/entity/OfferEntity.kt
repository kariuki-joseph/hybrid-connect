package com.example.hybridconnect.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hybridconnect.domain.enums.OfferTag
import com.example.hybridconnect.domain.enums.OfferType
import java.util.UUID

@Entity(tableName = "offers")
data class OfferEntity(
    @PrimaryKey val id: UUID,
    var name: String,
    var ussdCode: String,
    var price: Int,
    @ColumnInfo(defaultValue = "VOICE")
    var type: OfferType,
    val tag: OfferTag? = null,
    val isSiteLinked: Boolean = false
)