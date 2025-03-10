package com.example.hybridconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.hybridconnect.data.local.entity.AppOfferEntity
import com.example.hybridconnect.data.local.entity.OfferEntity
import java.util.UUID

@Dao
interface AppOfferDao {

    @Insert
    suspend fun addAppOffer(entity: AppOfferEntity)

    @Query("DELETE FROM app_offers WHERE appId = :appId AND offerId = :offerId")
    suspend fun deleteAppOffer(appId: String, offerId: UUID)

    @Query(
        """
        SELECT appId FROM app_offers 
        WHERE offerId = :offerId 
        LIMIT 1
    """
    )
    suspend fun getAppByOffer(offerId: UUID): String?

    @Query(
        """
        SELECT * FROM offers 
        WHERE id IN (SELECT offerId FROM app_offers WHERE appId = :appId)
    """
    )
    suspend fun getOffersByAppId(appId: String): List<OfferEntity>
}