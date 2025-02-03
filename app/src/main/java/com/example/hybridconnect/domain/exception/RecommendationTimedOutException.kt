package com.example.hybridconnect.domain.exception

class RecommendationTimedOutException(val phoneNumber: String): Exception("Recommendation timed out for $phoneNumber")