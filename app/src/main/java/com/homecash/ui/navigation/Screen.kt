package com.homecash.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    
    // Stitch 3-Step Guided Onboarding
    object Step1 : Screen("step1")
    object Step2 : Screen("step2")
    object Step3 : Screen("step3")
    
    // Bottom Bar Destinations
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object Notifications : Screen("notifications")
    
    // Action Screens
    object ShiftClosing : Screen("shift_closing")
    object FlagResolution : Screen("flag_resolution")
}
