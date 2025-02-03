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
import com.example.hybridconnect.domain.enums.AutoReplyType
import com.example.hybridconnect.presentation.ui.screens.AddOfferScreen
import com.example.hybridconnect.presentation.ui.screens.AutoReplyScreen
import com.example.hybridconnect.presentation.ui.screens.CreateSiteLinkScreen
import com.example.hybridconnect.presentation.ui.screens.EditAutoReplyMessageScreen
import com.example.hybridconnect.presentation.ui.screens.EditCustomerScreen
import com.example.hybridconnect.presentation.ui.screens.EditOfferScreen
import com.example.hybridconnect.presentation.ui.screens.EditProfileScreen
import com.example.hybridconnect.presentation.ui.screens.EditSiteLinkScreen
import com.example.hybridconnect.presentation.ui.screens.HomeScreen
import com.example.hybridconnect.presentation.ui.screens.HybridConnectScreen
import com.example.hybridconnect.presentation.ui.screens.LoginScreen
import com.example.hybridconnect.presentation.ui.screens.MyCustomersScreen
import com.example.hybridconnect.presentation.ui.screens.MySubscriptionScreen
import com.example.hybridconnect.presentation.ui.screens.OffersScreen
import com.example.hybridconnect.presentation.ui.screens.OnBoardingScreen1
import com.example.hybridconnect.presentation.ui.screens.OnBoardingScreen2
import com.example.hybridconnect.presentation.ui.screens.OtpVerificationScreen
import com.example.hybridconnect.presentation.ui.screens.PayWithAirtimeScreen
import com.example.hybridconnect.presentation.ui.screens.PayWithMpesaScreen
import com.example.hybridconnect.presentation.ui.screens.PayWithScreen
import com.example.hybridconnect.presentation.ui.screens.PinLoginScreen
import com.example.hybridconnect.presentation.ui.screens.PinSetupScreen
import com.example.hybridconnect.presentation.ui.screens.QuickDialScreen
import com.example.hybridconnect.presentation.ui.screens.RegisterScreen
import com.example.hybridconnect.presentation.ui.screens.RescheduleOfferScreen
import com.example.hybridconnect.presentation.ui.screens.ResetPasswordScreen
import com.example.hybridconnect.presentation.ui.screens.SettingsScreen
import com.example.hybridconnect.presentation.ui.screens.SimSetup
import com.example.hybridconnect.presentation.ui.screens.SiteLinkScreen
import com.example.hybridconnect.presentation.ui.screens.SplashScreen
import com.example.hybridconnect.presentation.ui.screens.TransactionDetailsScreen
import com.example.hybridconnect.presentation.ui.screens.TransactionsScreen
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
        composable(Route.SimSetup.name) {
            SimSetup(
                onSimSetup = {
                    navController.navigate(Route.Home.name)
                }
            )
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
        composable(Route.TransactionDetails.name) {
            val transactionId = it.arguments?.getString("transactionId")
            transactionId?.let { id ->
                TransactionDetailsScreen(
                    transactionId = UUID.fromString(id),
                    navController = navController
                )
            }
        }
        composable(Route.MySubscription.name) {
            MySubscriptionScreen(navController = navController)
        }
        composable(Route.PayWithMpesa.name) {
            PayWithMpesaScreen(
                navController = navController,
            )
        }
        composable(Route.EditProfile.name) {
            EditProfileScreen(navController = navController)
        }
        composable(Route.SiteLink.name) {
            SiteLinkScreen(navController = navController)
        }
        composable(Route.Customers.name) {
            MyCustomersScreen(navController = navController)
        }
        composable(Route.QuickDial.name) {
            QuickDialScreen(navController = navController)
        }
        composable(Route.Login.name) {
            LoginScreen(navController = navController)
        }
        composable(Route.PayWith.name) {
            PayWithScreen(navController = navController)
        }
        composable(Route.PayWithAirtime.name) {
            PayWithAirtimeScreen(navController = navController)
        }
        composable(Route.Transactions.name) {
            TransactionsScreen(navController = navController)
        }
        composable(Route.AutoReply.name) {
            AutoReplyScreen(navController = navController)
        }
        composable(Route.RescheduleOffer.name) {
            val transactionId = it.arguments?.getString("transactionId")
            transactionId?.let { id ->
                RescheduleOfferScreen(
                    navController = navController,
                    transactionId = id
                )
            }
        }
        composable(Route.EditCustomer.name) {
            val customerPhone = it.arguments?.getString("customerPhone")
            customerPhone?.let { phone ->
                EditCustomerScreen(
                    navController = navController,
                    customerPhone = phone
                )
            }
        }
        composable(Route.EditAutoReplyMessage.name) {
            val autoReplyType = it.arguments?.getString("type")
            autoReplyType?.let { type ->
                EditAutoReplyMessageScreen(
                    navController = navController,
                    autoReplyType = AutoReplyType.valueOf(type)
                )
            }
        }
        composable(Route.ResetPassword.name) {
            val email = it.arguments?.getString("email") ?: ""
            ResetPasswordScreen(
                navController = navController,
                passedEmail = email
            )
        }
        composable(Route.CreateSiteLink.name) {
            CreateSiteLinkScreen(navController = navController)
        }
        composable(Route.EditSiteLink.name) {
            EditSiteLinkScreen(navController = navController)
        }
        composable(Route.HybridConnect.name) {
            HybridConnectScreen(navController = navController)
        }
    }
}