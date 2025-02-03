package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.SiteLinkEntity
import com.example.hybridconnect.domain.model.SiteLink

fun SiteLink.toEntity(): SiteLinkEntity {
    return SiteLinkEntity(
        id = this.id,
        siteName = this.siteName,
        accountType = this.accountType,
        accountNumber = this.accountNumber,
        siteLinkURL = this.url,
        isActive = this.isActive
    )
}