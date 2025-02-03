package com.example.hybridconnect.domain.usecase

class FormatUssdUseCase {
    operator fun invoke(ussdCode: String, phone: String): String {
        return if (ussdCode.contains("BH")) ussdCode.replace("BH", phone) else ussdCode
    }
}