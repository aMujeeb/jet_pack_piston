package com.mujapps.piston.view.navigation

sealed class DestinationScreen(val route : String) {
    data object SignUp : DestinationScreen("signup")
    data object Login : DestinationScreen("login")
    data object Profile : DestinationScreen("profile")
    data object Swipe : DestinationScreen("swipe")
    data object ChatList : DestinationScreen("chatList")
    data object SingleChat : DestinationScreen("singleChat/{chatId}") {
        fun createRoute(id : String) = "singleChat/$id"
    }
}