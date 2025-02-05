package com.example.hybridconnect.domain.exception

class RecommendationTimedOutException(val msg: String): Exception("Recommendation timed with message $msg")