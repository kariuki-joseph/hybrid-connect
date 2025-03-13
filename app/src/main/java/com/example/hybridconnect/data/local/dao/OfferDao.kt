package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.hybridconnect.data.local.entity.OfferEntity
import java.util.UUID

@Dao
interface OfferDao {
    @Insert
    suspend fun insert(offer: OfferEntity)

    @Insert
    suspend fun insertAll(offers: List<OfferEntity>)

    @Query("SELECT * FROM offers")
    suspend fun getAllOffers(): List<OfferEntity>

    @Query("SELECT * FROM offers WHERE price = :price")
    suspend fun getOfferByPrice(price: Int): OfferEntity?

    @Query("SELECT * FROM offers WHERE id = :id")
    suspend fun getOfferById(id: UUID): OfferEntity?

    @Query("DELETE FROM offers WHERE id = :id")
    suspend fun deleteOfferById(id: UUID)

    @Update
    suspend fun update(offer: OfferEntity)
}