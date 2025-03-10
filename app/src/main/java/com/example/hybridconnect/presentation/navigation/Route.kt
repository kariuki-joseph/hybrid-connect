package com.example.hybridconnect.presentation.navigation

sealed class Route(val name: String, val title: String) {
    data object SplashScreen : Route("splash-screen", "Splash Screen")
    data object OnboardingScreen1 : Route("onboarding-screen-1", "Onboarding Screen")
    data object OnboardingScreen2 : Route("onboarding-screen-2", "Onboarding Screen 2")
    data object Register : Route("register", "Create Account")
    data object OtpVerification : Route("verify-otp/{email}", "Otp Verification")
    data object PinSetup : Route("setup-pin", "Setup Pin")
    data object PinLogin: Route("pin-login/{email}", "Pin Login")
    data object Home : Route("home", "Home")
    data object Offers : Route("offers", "Offers")
    data object AddOffer : Route("offers/new", "Add Offer")
    data object EditOffer : Route("offers/edit/{offerId}", "Edit Offer")
    data object Settings : Route("settings", "Settings")
    data object EditProfile : Route("profile/edit", "Edit Profile")
    data object Login: Route("login", "Login")
    data object ResetPassword: Route("reset-password/{email}", "Reset Password")
    data object AddConnectedApp: Route("hybrid-connect/new", "Add Connected App")
}