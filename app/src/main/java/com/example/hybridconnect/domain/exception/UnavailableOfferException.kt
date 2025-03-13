package com.example.hybridconnect.domain.exception

import com.example.hybridconnect.domain.model.Transaction

class UnavailableOfferException(
    val transaction: Transaction,
    message: String
): Exception(message)