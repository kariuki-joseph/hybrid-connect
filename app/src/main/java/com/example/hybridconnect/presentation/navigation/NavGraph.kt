package com.example.hybridconnect.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hybridconnect.presentation.ui.screens.AddConnectedAppScreen
import com.example.hybridconnect.presentation.ui.screens.AddOfferScreen
import com.example.hybridconnect.presentation.ui.screens.AppDetailsScreen
import com.example.hybridconnect.presentation.ui.screens.EditOfferScreen
import com.example.hybridconnect.presentation.ui.screens.EditProfileScreen
import com.example.hybridconnect.presentation.ui.screens.HomeScreen
import com.example.hybridconnect.presentation.ui.screens.LoginScreen
import com.example.hybridconnect.presentation.ui.screens.OffersScreen
import com.example.hybridconnect.presentation.ui.screens.OnBoardingScreen1
import com.example.hybridconnect.presentation.ui.screens.OnBoardingScreen2
import com.example.hybridconnect.presentation.ui.screens.OtpVerificationScreen
import com.example.hybridconnect.presentation.ui.screens.PinLoginScreen
import com.example.hybridconnect.presentation.ui.screens.PinSetupScreen
import com.example.hybridconnect.presentation.ui.screens.RegisterScreen
import com.example.hybridconnect.presentation.ui.screens.ResetPasswordScreen
import com.example.hybridconnect.presentation.ui.screens.SettingsScreen
import com.example.hybridconnect.presentation.ui.screens.SplashScreen
import com.example.hybridconnect.presentation.viewmodel.SettingsViewModel
import java.util.UUID

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val isNavigated = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingsViewModel.getInitialDestination()
        isNavigated.value = true
    }

    // Collect the onboarding completion status
    val startRoute by settingsViewModel.initialRoute.collectAsState()

    val startDestination = if (isNavigated.value) {
        startRoute.name
    } else {
        Route.SplashScreen.name
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Route.SplashScreen.name) {
            SplashScreen()
        }
        composable(Route.OnboardingScreen1.name) {
            OnBoardingScreen1(
                onSkip = {
                    navController.navigate(Route.Login.name)
                },
                onNext = {
                    settingsViewModel.setOnboardingCompleted()
                    navController.navigate(Route.Login.name)
                },
            )
        }
        composable(Route.OnboardingScreen2.name) {
            OnBoardingScreen2(
                onPrev = {
                    navController.popBackStack()
                },
                onNext = {
                    settingsViewModel.setOnboardingCompleted()
                    navController.navigate(Route.Register.name)
                }
            )
        }
        composable(Route.Register.name) {
            RegisterScreen(navController = navController)
        }
        composable(Route.OtpVerification.name) {
            val email = it.arguments?.getString("email") ?: "user@example.com"
            OtpVerificationScreen(navController = navController, email = email)
        }
        composable(Route.PinLogin.name) {
            val email = it.arguments?.getString("email") ?: ""
            PinLoginScreen(navController = navController, email = email)
        }
        composable(Route.PinSetup.name) {
            PinSetupScreen(navController = navController)
        }
        composable(Route.Home.name) {
            HomeScreen(
                modifier = Modifier,
                navController = navController
            )
        }
        composable(Route.Offers.name) {
            OffersScreen(
                modifier = Modifier,
                navController = navController
            )
        }
        composable(Route.AddOffer.name) {
            AddOfferScreen(
                navController = navController
            )
        }
        composable(Route.EditOffer.name) {
            val offerId = it.arguments?.getString("offerId")
            offerId?.let { id ->
                EditOfferScreen(
                    offerId = UUID.fromString(id),
                    navController = navController
                )
            }
        }
        composable(Route.Settings.name) {
            SettingsScreen(
                navController = navController
            )
        }
        composable(Route.EditProfile.name) {
            EditProfileScreen(navController = navController)
        }
        composable(Route.Login.name) {
            LoginScreen(navController = navController)
        }
        composable(Route.AddConnectedApp.name) {
            AddConnectedAppScreen(navController = navController)
        }
        composable(Route.AppDetails.name) {
            val connectId = it.arguments?.getString("connectId")
            if (connectId != null) {
                AppDetailsScreen(navController = navController, connectId = connectId)
            }
        }
        composable(Route.ResetPassword.name) {
            val email = it.arguments?.getString("email") ?: ""
            ResetPasswordScreen(
                navController = navController,
                passedEmail = email
            )
        }
    }
}