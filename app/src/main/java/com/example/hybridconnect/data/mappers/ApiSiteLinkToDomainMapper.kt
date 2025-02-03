package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.remote.api.response.ApiSiteLink
import com.example.hybridconnect.domain.model.SiteLink
import java.util.UUID

fun ApiSiteLink.toDomain(): SiteLink {
    return SiteLink(
        id = this.siteLinkId,
        siteName = this.siteName,
        url = this.siteLinkUrl,
        accountType = this.accountType,
        accountNumber = this.accountNumber,
        isActive = this.isActive
    )
}