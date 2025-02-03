package com.example.hybridconnect.presentation.dto

sealed class DurationFilter {
    data object Today: DurationFilter()
    data object Yesterday: DurationFilter()
    data object Last7Days: DurationFilter()
    data object Last30Days: DurationFilter()
}