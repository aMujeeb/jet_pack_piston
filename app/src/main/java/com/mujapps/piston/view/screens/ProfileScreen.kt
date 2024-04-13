package com.mujapps.piston.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mujapps.piston.data.UserData
import com.mujapps.piston.view.components.BottomNavigationItem
import com.mujapps.piston.view.components.BottomNavigationMenu
import com.mujapps.piston.view.components.CommonDivider
import com.mujapps.piston.view.components.CommonProgressSpinner
import com.mujapps.piston.view.main.MainViewModel
import com.mujapps.piston.view.navigation.DestinationScreen
import com.mujapps.piston.view.navigation.navigateTo

enum class Gender {
    CAT, DOG, ANY
}

@Composable
fun ProfileScreen(mNavController: NavController, mMainViewModel: MainViewModel = hiltViewModel()) {

    val mIsLoadingState by mMainViewModel.mInProgressState.collectAsStateWithLifecycle()

    if (mIsLoadingState) {
        CommonProgressSpinner()
    } else {
        //All Components
        val mUserData = mMainViewModel.mUserDataState.collectAsStateWithLifecycle().value

        var name by rememberSaveable {
            mutableStateOf(mUserData?.name ?: "")
        }

        var userName by rememberSaveable {
            mutableStateOf(mUserData?.userName ?: "")
        }

        var bio by rememberSaveable {
            mutableStateOf(mUserData?.bio ?: "")
        }

        var gender by rememberSaveable {
            mutableStateOf(Gender.valueOf(mUserData?.gender?.uppercase() ?: "CAT"))
        }

        var genderPreference by rememberSaveable {
            mutableStateOf(Gender.valueOf(mUserData?.genderPreference?.uppercase() ?: "DOG"))
        }

        val scrollState = rememberScrollState()

        Column {
            ProfileContent(
                mModifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(8.dp),
                mMainViewModel = mMainViewModel,
                mUserData = mUserData,
                onNameChanged = {
                    name = it
                },
                onUserNameChanged = {
                    userName = it
                },
                onBioChanged = {
                    bio = it
                },
                onGenderChanged = {
                    gender = it
                },
                onGenderPreferenceChanged = {
                    genderPreference = it
                },
                onSaveClicked = {
                    //Trigger View Model
                },
                onBackPress = {
                    navigateTo(mNavController, DestinationScreen.Swipe.route)
                },
                onLogOut = {
                    navigateTo(mNavController, DestinationScreen.Login.route)
                }
            )
            BottomNavigationMenu(selectedItem = BottomNavigationItem.PROFILE, navController = mNavController)
        }
    }
}

@Composable
fun ProfileContent(
    mModifier: Modifier = Modifier,
    mMainViewModel: MainViewModel,
    mUserData: UserData?,
    onNameChanged: (String) -> Unit,
    onUserNameChanged: (String) -> Unit,
    onBioChanged: (String) -> Unit,
    onGenderChanged: (Gender) -> Unit,
    onGenderPreferenceChanged: (Gender) -> Unit,
    onSaveClicked: () -> Unit,
    onBackPress: () -> Unit,
    onLogOut: () -> Unit
) {
    Column(modifier = mModifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(text = "Back", modifier = Modifier.clickable {
                onBackPress.invoke()
            })

            Text(text = "Save", modifier = Modifier.clickable {
                onSaveClicked.invoke()
            })
        }

        CommonDivider()

        ProfileImage()

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name", modifier = Modifier.width(100.dp))
            TextField(
                value = mUserData?.name ?: "",
                onValueChange = onNameChanged,
                modifier = Modifier.background(Color.Transparent),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "User Name", modifier = Modifier.width(100.dp))
            TextField(
                value = mUserData?.userName ?: "",
                onValueChange = onUserNameChanged,
                modifier = Modifier.background(Color.Transparent),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Bio", modifier = Modifier.width(100.dp))
            TextField(
                value = mUserData?.bio ?: "",
                onValueChange = onBioChanged,
                modifier = Modifier
                    .background(Color.Transparent)
                    .height(150.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black
                ),
                singleLine = false
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "I am a :", modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = Gender.valueOf(mUserData?.gender ?: "CAT") == Gender.CAT, onClick = { onGenderChanged(Gender.CAT) })
                    Text(text = "Cat", modifier = Modifier
                        .padding(4.dp)
                        .clickable { onGenderChanged(Gender.CAT) })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = Gender.valueOf(mUserData?.gender ?: "DOG") == Gender.DOG, onClick = { onGenderChanged(Gender.DOG) })
                    Text(text = "Dog", modifier = Modifier
                        .padding(4.dp)
                        .clickable { onGenderChanged(Gender.DOG) })
                }
            }
        }

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "I am looking for :", modifier = Modifier
                    .width(100.dp)
                    .padding(8.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = Gender.valueOf(mUserData?.gender ?: "CAT") == Gender.CAT,
                        onClick = { onGenderPreferenceChanged(Gender.CAT) })
                    Text(text = "Cats", modifier = Modifier
                        .padding(4.dp)
                        .clickable { onGenderPreferenceChanged(Gender.CAT) })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = Gender.valueOf(mUserData?.gender ?: "DOG") == Gender.DOG,
                        onClick = { onGenderPreferenceChanged(Gender.DOG) })
                    Text(text = "Dogs", modifier = Modifier
                        .padding(4.dp)
                        .clickable { onGenderPreferenceChanged(Gender.DOG) })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = Gender.valueOf(mUserData?.gender ?: "ANY") == Gender.ANY,
                        onClick = { onGenderPreferenceChanged(Gender.ANY) })
                    Text(text = "Any", modifier = Modifier
                        .padding(4.dp)
                        .clickable { onGenderPreferenceChanged(Gender.ANY) })
                }
            }
        }

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {

            Text(text = "LogOut", modifier = Modifier.clickable {
                onLogOut.invoke()
            })
        }
    }
}

@Composable
fun ProfileImage() {

}