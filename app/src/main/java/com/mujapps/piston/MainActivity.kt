package com.mujapps.piston

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.mujapps.piston.data.MockData
import com.mujapps.piston.ui.theme.PistonTheme
import com.mujapps.piston.utils.LoggerUtils
import com.mujapps.piston.view.components.ProfileScreen
import com.mujapps.piston.view.components.SwipeCard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PistonTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProfileSelector()
                }
            }
        }
    }
}

@Composable
fun ProfileSelector() {
    Column {
        MockData.profiles.forEach {

            val showLeftSwipe = rememberSaveable {
                mutableStateOf(false)
            }

            val showRightSwipe = rememberSaveable {
                mutableStateOf(false)
            }

            SwipeCard(onSwipeLeft = {
                LoggerUtils.logMessage("To Left")
                showLeftSwipe.value = true
                showRightSwipe.value = false
            }, onSwipeRight = {
                LoggerUtils.logMessage("To Right")
                showRightSwipe.value = true
                showLeftSwipe.value = false
            }) {
                ProfileScreen(it.name, it.drawableResId, showLeftSwipe, showRightSwipe)
            }
        }
    }
}