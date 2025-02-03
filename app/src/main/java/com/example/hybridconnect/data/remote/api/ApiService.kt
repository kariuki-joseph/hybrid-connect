package com.example.hybridconnect.data.remote.api

import com.example.hybridconnect.data.remote.api.request.AddSiteLinkOfferRequest
import com.example.hybridconnect.data.remote.api.request.ResendOtpRequest
import com.example.hybridconnect.data.remote.api.request.SignInRequest
import com.example.hybridconnect.data.remote.api.request.SignUpRequest
import com.example.hybridconnect.data.remote.api.request.SiteLinkRequest
import com.example.hybridconnect.data.remote.api.request.StkRequest
import com.example.hybridconnect.data.remote.api.request.UpdateProfileRequest
import com.example.hybridconnect.data.remote.api.request.UpdateSiteLinkRequest
import com.example.hybridconnect.data.remote.api.request.PostSubscriptionPaymentRequest
import com.example.hybridconnect.data.remote.api.request.SyncSubscriptionRequest
import com.example.hybridconnect.data.remote.api.request.VerifyOtpRequest
import com.example.hybridconnect.data.remote.api.response.ActiveSubscriptionResponse
import com.example.hybridconnect.data.remote.api.response.AdminPhoneResponse
import com.example.hybridconnect.data.remote.api.response.ApiAgent
import com.example.hybridconnect.data.remote.api.response.ApiResponse
import com.example.hybridconnect.data.remote.api.response.CommissionRateResponse
import com.example.hybridconnect.data.remote.api.response.HybridConnectResponse
import com.example.hybridconnect.data.remote.api.response.ResendOtpResponse
import com.example.hybridconnect.data.remote.api.response.SignInResponse
import com.example.hybridconnect.data.remote.api.response.SignUpResponse
import com.example.hybridconnect.data.remote.api.response.SiteLinkDetailsResponse
import com.example.hybridconnect.data.remote.api.response.SiteLinkResponse
import com.example.hybridconnect.data.remote.api.response.SubscriptionResponse
import com.example.hybridconnect.data.remote.api.response.UpdatePinRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("signup")
    suspend fun signUp(
        @Body request: SignUpRequest,
    ): retrofit2.Response<ApiResponse<SignUpResponse>>

    @POST("signin")
    suspend fun signIn(
        @Body request: SignInRequest,
    ): retrofit2.Response<ApiResponse<SignInResponse>>

    @POST("verify")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest,
    ): retrofit2.Response<ApiResponse<ApiAgent>>

    @POST("resendcode")
    suspend fun resendOtp(
        @Body request: ResendOtpRequest,
    ): retrofit2.Response<ApiResponse<ResendOtpResponse>>

    @PUT("updatePin")
    suspend fun updatePin(
        @Body request: UpdatePinRequest
    ): retrofit2.Response<ApiResponse<Unit>>

    @POST("stk")
    suspend fun sendStkPush(
        @Body stkRequest: StkRequest,
    ): retrofit2.Response<ApiResponse<Unit>>

    @GET("rates")
    suspend fun getCommissionRates(): retrofit2.Response<ApiResponse<List<CommissionRateResponse>>>

    @POST("sitelink")
    suspend fun requestSiteLink(
        @Body siteLinkRequest: SiteLinkRequest,
    ): retrofit2.Response<ApiResponse<SiteLinkResponse>>

    @DELETE("sitelink/{siteLinkId}")
    suspend fun deleteSiteLink(
        @Path("siteLinkId") siteLinkId: String
    ): retrofit2.Response<ApiResponse<Unit>>

    @PUT("sitelink/{siteLinkId}")
    suspend fun updateSiteLink(
        @Path("siteLinkId") sitLinkId: String,
        @Body request: UpdateSiteLinkRequest
    ): retrofit2.Response<ApiResponse<Unit>>

    @DELETE("offers/{offerId}")
    suspend fun deleteSiteLinkOffer(
        @Path("offerId") offerId: String
    ): retrofit2.Response<ApiResponse<Unit>>

    @POST("offers")
    suspend fun addSiteLinkOffer(
        @Body request: AddSiteLinkOfferRequest
    ): retrofit2.Response<ApiResponse<Unit>>

    @GET("packages")
    suspend fun getSubscriptions(): retrofit2.Response<ApiResponse<List<SubscriptionResponse>>>

    @POST("subscriptions/new")
    suspend fun postSubscriptionPayment(
        @Body request: PostSubscriptionPaymentRequest
    ): retrofit2.Response<ApiResponse<Unit>>

    @POST("subscriptions/sync")
    suspend fun syncSubscriptionPlan(
        @Body request: SyncSubscriptionRequest
    ): retrofit2.Response<ApiResponse<List<Unit>>>

    @PUT("manage/agent/{agentId}")
    suspend fun updateProfile(
        @Path("agentId") agentId: String,
        @Body request: UpdateProfileRequest
    ): retrofit2.Response<ApiResponse<Unit>>

    @POST("sitelink/{linkId}/activate")
    suspend fun activateSiteLink(
        @Path("linkId") linkId: String
    ): retrofit2.Response<ApiResponse<Unit>>

    @POST("sitelink/{linkId}/deactivate")
    suspend fun deactivateSiteLink(
        @Path("linkId") linkId: String
    ): retrofit2.Response<ApiResponse<Unit>>

    @GET("sitelink/{linkId}")
    suspend fun getSiteLinkDetails(
       @Path("linkId") linkId: String
    ): retrofit2.Response<ApiResponse<SiteLinkDetailsResponse>>

    @GET("subscriptionNumbers")
    suspend fun getSubscriptionNumbers(): retrofit2.Response<ApiResponse<List<AdminPhoneResponse>>>

    @GET("sitelinkNumbers")
    suspend fun getSiteLinkNumbers(): retrofit2.Response<ApiResponse<List<AdminPhoneResponse>>>

    @POST("hybrid-connect/generate")
    suspend fun generateHybridConnectId(): retrofit2.Response<ApiResponse<HybridConnectResponse>>
}
