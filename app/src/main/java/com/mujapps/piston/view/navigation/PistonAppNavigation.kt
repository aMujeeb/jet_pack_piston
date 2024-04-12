package com.mujapps.piston.view.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mujapps.piston.view.components.NotificationMessage
import com.mujapps.piston.view.main.MainViewModel
import com.mujapps.piston.view.screens.ChatListScreen
import com.mujapps.piston.view.screens.LoginScreen
import com.mujapps.piston.view.screens.ProfileScreen
import com.mujapps.piston.view.screens.SignUpScreen
import com.mujapps.piston.view.screens.SingleChatScreen
import com.mujapps.piston.view.screens.SwipeScreen

@Composable
fun PistonAppNavigation() {
    val navController = rememberNavController()

    val vm: MainViewModel = hiltViewModel()
    NotificationMessage(viewModel = vm)

    NavHost(navController = navController, startDestination = DestinationScreen.SignUp.route) {
        composable(DestinationScreen.SignUp.route) {
            SignUpScreen(navController)
        }

        composable(DestinationScreen.Login.route) {
            LoginScreen()
        }

        composable(DestinationScreen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(DestinationScreen.Swipe.route) {
            SwipeScreen(navController)
        }

        composable(DestinationScreen.ChatList.route) {
            ChatListScreen(navController)
        }

        composable(DestinationScreen.SingleChat.route) {
            SingleChatScreen(chatId = "123")
        }
    }
}

//Keeping Single navigation stack on bottom tab clicks. To avoid huge backstack
fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}