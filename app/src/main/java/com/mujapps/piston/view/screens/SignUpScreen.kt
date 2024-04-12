package com.mujapps.piston.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.mujapps.piston.R
import com.mujapps.piston.view.components.CommonProgressSpinner
import com.mujapps.piston.view.main.MainViewModel
import com.mujapps.piston.view.navigation.DestinationScreen


@Preview
@Composable
fun SignUpScreen(mNavController: NavController = rememberNavController(), mMainViewModel: MainViewModel = hiltViewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val mUserNameState = remember {
                mutableStateOf(TextFieldValue())
            }

            val mEmailState = remember {
                mutableStateOf(TextFieldValue())
            }

            val mPasswordState = remember {
                mutableStateOf(TextFieldValue())
            }

            val mFocus = LocalFocusManager.current

            val mIsLoadingState by mMainViewModel.mInProgressState.collectAsStateWithLifecycle()

            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "Sign up Screen logo",
                modifier = Modifier
                    .width(200.dp)
                    .padding(16.dp)
                    .padding(top = 24.dp)
            )

            Text(
                text = "Sign Up",
                modifier = Modifier
                    .padding(8.dp)
                    .padding(top = 16.dp),
                style = TextStyle(fontSize = 30.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = mUserNameState.value,
                onValueChange = { mUserNameState.value = it },
                modifier = Modifier.padding(16.dp),
                singleLine = true,
                label = {
                    Text(
                        text = "User Name",
                        style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, color = Color.Black)
                    )
                }, colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black
                )
            )

            OutlinedTextField(
                value = mEmailState.value,
                onValueChange = { mEmailState.value = it },
                modifier = Modifier.padding(16.dp),
                singleLine = true,
                label = {
                    Text(
                        text = "Email",
                        style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, color = Color.Black)
                    )
                }, colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black
                )
            )

            OutlinedTextField(
                value = mPasswordState.value,
                onValueChange = { mPasswordState.value = it },
                modifier = Modifier.padding(16.dp),
                singleLine = true,
                label = {
                    Text(
                        text = "Password",
                        style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, color = Color.Black)
                    )
                }, colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black
                )
            )


            Button(
                onClick = {
                    mFocus.clearFocus(true)
                    mMainViewModel.signUpUser(mUserNameState.value.text, mEmailState.value.text, mPasswordState.value.text)
                }, modifier = Modifier
                    .padding(start = 48.dp, end = 48.dp, top = 24.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Sign Up", style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold))
            }


            Text(
                text = "Already a user? Go to login..!",
                style = TextStyle(fontSize = 14.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, color = Color.Blue),
                modifier = Modifier
                    .padding(start = 48.dp, end = 48.dp, top = 24.dp)
                    .clickable {
                        mNavController.navigate(DestinationScreen.Login.route)
                    }
            )

            if (mIsLoadingState) {
                CommonProgressSpinner()
            }
        }
    }
}