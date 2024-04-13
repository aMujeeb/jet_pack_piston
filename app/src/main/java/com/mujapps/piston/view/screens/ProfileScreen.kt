package com.mujapps.piston.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mujapps.piston.view.components.BottomNavigationItem
import com.mujapps.piston.view.components.BottomNavigationMenu

enum class Gender {
    CAT, DOG, ANY
}

@Composable
fun ProfileScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Text(text = "Profile Screen")
        BottomNavigationMenu(selectedItem = BottomNavigationItem.PROFILE, navController = navController)
    }
}