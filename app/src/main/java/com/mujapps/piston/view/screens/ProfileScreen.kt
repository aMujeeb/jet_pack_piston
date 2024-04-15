package com.mujapps.piston.view.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mujapps.piston.R
import com.mujapps.piston.data.UserData
import com.mujapps.piston.utils.LoggerUtils
import com.mujapps.piston.view.components.BottomNavigationItem
import com.mujapps.piston.view.components.BottomNavigationMenu
import com.mujapps.piston.view.components.CommonDivider
import com.mujapps.piston.view.components.CommonImage
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
        val mGender = if (mUserData?.gender.isNullOrEmpty()) "CAT" else mUserData?.gender!!.uppercase()
        val mGenderPreference = if (mUserData?.genderPreference.isNullOrEmpty()) "CAT" else mUserData.genderPreference!!.uppercase()

        LoggerUtils.logMessage(mUserData?.userName ?: "uihh")

        var name by rememberSaveable {
            mutableStateOf(mUserData?.name ?: "")
        }

        var userName by rememberSaveable {
            mutableStateOf(mUserData?.userName ?: "")
        }

        var bio by rememberSaveable {
            mutableStateOf(mUserData?.bio ?: "")
        }

        var profileImage by rememberSaveable {
            mutableStateOf(mUserData?.imageUrl ?: "")
        }

        var gender by rememberSaveable {
            mutableStateOf(Gender.valueOf(mGender))
        }

        var genderPreference by rememberSaveable {
            mutableStateOf(Gender.valueOf(mGenderPreference))
        }

        val scrollState = rememberScrollState()

        Column {
            ProfileContent(
                mModifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(8.dp),
                mMainViewModel = mMainViewModel,
                mName = name,
                mUserName = userName,
                mBio = bio,
                mProfileImage = profileImage,
                mGender = gender,
                mGenderPref = genderPreference,
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
                    mMainViewModel.updateProfileData(name, userName, bio, gender, genderPreference)
                },
                onBackPress = {
                    navigateTo(mNavController, DestinationScreen.Swipe.route)
                },
                onLogOut = {
                    mMainViewModel.onLogOut()
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
    mName: String,
    mUserName: String,
    mBio: String,
    mProfileImage: String,
    mGender: Gender,
    mGenderPref: Gender,
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

        ProfileImage(mProfileImage, mMainViewModel)

        CommonDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name", modifier = Modifier.width(100.dp))
            TextField(
                value = mName,
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
                value = mUserName,
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
                value = mBio,
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
                    RadioButton(selected = mGender == Gender.CAT, onClick = { onGenderChanged(Gender.CAT) })
                    Text(text = "Cat", modifier = Modifier
                        .padding(4.dp)
                        .clickable { onGenderChanged(Gender.CAT) })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = mGenderPref == Gender.DOG, onClick = { onGenderChanged(Gender.DOG) })
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
                        selected = mGenderPref == Gender.CAT,
                        onClick = { onGenderPreferenceChanged(Gender.CAT) })
                    Text(text = "Cats", modifier = Modifier
                        .padding(4.dp)
                        .clickable { onGenderPreferenceChanged(Gender.CAT) })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = mGenderPref == Gender.DOG,
                        onClick = { onGenderPreferenceChanged(Gender.DOG) })
                    Text(text = "Dogs", modifier = Modifier
                        .padding(4.dp)
                        .clickable { onGenderPreferenceChanged(Gender.DOG) })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = mGenderPref == Gender.ANY,
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
fun ProfileImage(imageUrl: String, viewModel: MainViewModel) {

    val contentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                viewModel.uploadProfilePic(uri)
            }
        }

    Box(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    contentLauncher.launch("image/*")
                }, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = CircleShape, modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = imageUrl,
                    contentDescription = "Profile pic",
                    alignment = Alignment.Center,
                    placeholder = painterResource(id = R.drawable.baseline_downloading)
                )
            }
            Text(text = "Change Profile Pic")
        }
    }
}