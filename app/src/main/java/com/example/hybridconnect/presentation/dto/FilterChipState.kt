package com.example.hybridconnect.presentation.dto

sealed class FilterChipState {
    data object All : FilterChipState() // No count for "All"
    data class Successful(val count: Int) : FilterChipState()
    data class Failed(val count: Int) : FilterChipState()
    data class Unmatched(val count: Int) : FilterChipState()
    data class Scheduled(val count: Int): FilterChipState()
    data class SiteLink(val count: Int) : FilterChipState()
}
