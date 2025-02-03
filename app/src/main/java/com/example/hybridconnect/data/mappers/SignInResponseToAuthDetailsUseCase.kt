package com.example.hybridconnect.data.mappers

import com.example.hybridconnect.data.remote.api.response.SignInResponse
import com.example.hybridconnect.domain.model.AuthDetails

fun SignInResponse.toAuthDetails(): AuthDetails {
    return AuthDetails(
        token = this.token,
        agent = this.agent.toDomain(),
        siteLink = this.siteLink?.toDomain()
    )
}