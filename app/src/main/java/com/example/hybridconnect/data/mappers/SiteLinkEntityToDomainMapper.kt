package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.local.entity.SiteLinkEntity
import com.example.hybridconnect.domain.model.SiteLink

fun SiteLinkEntity.toDomain(): SiteLink {
    return SiteLink(
        id = this.id,
        siteName = this.siteName,
        url = this.siteLinkURL,
        accountType = this.accountType,
        accountNumber = this.accountNumber,
        isActive = this.isActive
    )
}