package com.example.worknear.navigation

import ProfileScreen
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.worknear.network.RetrofitInstance
import com.example.worknear.screens.HomePage
import com.example.worknear.screens.HomeTab
import com.example.worknear.screens.LoginPage
import com.example.worknear.screens.PostJobTab

import com.example.worknear.screens.SearchTab
import com.example.worknear.screens.auth.RegistrationPage
import com.example.worknear.viewmodel.ProfileViewModel
import com.example.worknear.viewmodel.ProfileViewModelFactory

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val apiService = RetrofitInstance.apiService
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userId = prefs.getString("userId", "") ?: ""

    NavHost(navController = navController, startDestination = "login") {
        composable("register") { RegistrationPage(navController) }
        composable("login") { LoginPage(navController) }
        composable("home") {
            val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(apiService, userId))
            HomeTab(navController, profileViewModel)
        }
        composable("search") {
            val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(apiService, userId))
            SearchTab(navController, profileViewModel)
        }
        composable("post") { PostJobTab(navController) }
        composable("profile") {
            val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(apiService, userId))
            ProfileScreen(navController, profileViewModel)
        }
        composable("homepage") { HomePage(apiService, userId) }
    }
}


