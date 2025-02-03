package com.example.hybridconnect.domain.utils

import java.text.DecimalFormat

fun formatCurrency(amount: Int): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(amount)
}

fun formatPhoneToTenDigits(phone: String): String {
    val lastNineDigits = phone.takeLast(9)
    return "0$lastNineDigits"
}