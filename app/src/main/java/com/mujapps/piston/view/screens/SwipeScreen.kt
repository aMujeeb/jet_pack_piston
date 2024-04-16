package com.mujapps.piston.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mujapps.piston.R
import com.mujapps.piston.data.UserData
import com.mujapps.piston.domain.data.MatchProfile
import com.mujapps.piston.utils.LoggerUtils
import com.mujapps.piston.view.components.BottomNavigationItem
import com.mujapps.piston.view.components.BottomNavigationMenu
import com.mujapps.piston.view.components.CommonProgressSpinner
import com.mujapps.piston.view.components.Direction
import com.mujapps.piston.view.components.SwipeCard
import com.mujapps.piston.view.main.MainViewModel
import kotlinx.coroutines.launch

@Composable
private fun ProfileCard(
    modifier: Modifier,
    matchProfile: MatchProfile,
) {
    Card(modifier) {
        Box {
            Image(
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(matchProfile.drawableResId),
                contentDescription = null
            )
            Scrim(Modifier.align(Alignment.BottomCenter))
            Column(Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = matchProfile.name,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun Hint(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

/*@Composable
private fun TransparentSystemBars() {
    val systemUiController = rememberS
    val useDarkIcons = false

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false
        )
        onDispose {}
    }
}*/

private fun stringFrom(direction: Direction): String {
    return when (direction) {
        Direction.Left -> "Left 👈"
        Direction.Right -> "Right 👉"
        Direction.Up -> "Up 👆"
        Direction.Down -> "Down 👇"
    }
}


@Composable
fun Scrim(modifier: Modifier = Modifier) {
    Box(
        modifier
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
            .height(180.dp)
            .fillMaxWidth()
    )
}


@Composable
fun SwipeScreen(navController: NavController, mMainViewModel: MainViewModel = hiltViewModel()) {

    val inProgressState by mMainViewModel.mInProgressProfiles.collectAsStateWithLifecycle()

    val showLeftSwipe = rememberSaveable {
        mutableStateOf(false)
    }

    val showRightSwipe = rememberSaveable {
        mutableStateOf(false)
    }

    if (inProgressState) {
        Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
            CommonProgressSpinner()
        }
    } else {
        val mMatchSwipeScreenState = mMainViewModel.mMatchSwipeScreenState.collectAsStateWithLifecycle().value

        if (mMatchSwipeScreenState?.resetUI == true) {
            showLeftSwipe.value = false
            showRightSwipe.value = false
        }

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xfff68084), Color(0xffa6c0fe))))
        ) {
            //Spacer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${mMatchSwipeScreenState?.nowCount}/${mMatchSwipeScreenState?.totalCount}",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                )
            }

            //Cards

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight(0.8f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No Profiles Available", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp))
                }

                mMatchSwipeScreenState?.mData?.forEach { matchProfile ->

                    SwipeCard(onSwipeLeft = {
                        //Dislike Functionality
                        mMainViewModel.onSwiped(matchProfile.userId)
                    }, onSwipeRight = {
                        //like Functionality
                        mMainViewModel.onSwiped(matchProfile.userId)
                    }, onSwipeIntermediateLeft = {
                        showLeftSwipe.value = true
                        showRightSwipe.value = false
                        //LoggerUtils.logMessage("Mid Left")
                    }, onSwipeIntermediateRight = {
                        //LoggerUtils.logMessage("Mid Right")
                        showLeftSwipe.value = false
                        showRightSwipe.value = true
                    }, onSwipeCancelled = {
                        LoggerUtils.logMessage("Set Back")
                        showLeftSwipe.value = false
                        showRightSwipe.value = false
                    }) {
                        ProfileSelector(matchProfile, showLeftSwipe, showRightSwipe)

                        val scope = rememberCoroutineScope()
                        Row(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_cancel),
                                contentDescription = "Reject",
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        //Reject functionality
                                        //mMainViewModel.onDisLike(matchProfile)
                                        mMainViewModel.onSwiped(matchProfile.userId)
                                        showLeftSwipe.value = true
                                    }
                                })


                            Image(
                                painter = painterResource(id = R.drawable.baseline_done),
                                contentDescription = "Reject",
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        //Accept functionality
                                        // mMainViewModel.onLike(matchProfile)
                                        mMainViewModel.onSwiped(matchProfile.userId)
                                        showRightSwipe.value = true
                                    }
                                })
                        }
                    }
                }
            }

            //Bottom Nav Bar
            BottomNavigationMenu(selectedItem = BottomNavigationItem.SWIPE, navController = navController)
        }
    }
}

@Composable
fun ProfileSelector(matchProfile: UserData, showLeftSwipe: MutableState<Boolean>, showRightSwipe: MutableState<Boolean>) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box {
                AsyncImage(
                    model = matchProfile.imageUrl,
                    contentDescription = matchProfile.userName,
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.8f),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    clipToBounds = true,
                    error = painterResource(id = R.drawable.baseline_downloading)
                )

                Box(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.8f)
                        .background(
                            color = if (showLeftSwipe.value) Color.Red.copy(alpha = 0.3f) else if (showRightSwipe.value) Color.Green.copy(
                                alpha = 0.3f
                            ) else Color.White.copy(alpha = 0.0f)
                        )
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        if (showRightSwipe.value) {
                            Image(
                                modifier = Modifier.size(72.dp, 72.dp),
                                painter = painterResource(id = R.drawable.baseline_done),
                                contentDescription = "Reject"
                            )
                        }

                        if (showLeftSwipe.value) {
                            Image(
                                modifier = Modifier.size(72.dp, 72.dp),
                                painter = painterResource(id = R.drawable.baseline_cancel),
                                contentDescription = "Reject"
                            )
                        }
                    }
                }
            }

            Text(
                text = matchProfile.name ?: matchProfile.userName ?: "",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            )
            Text(
                text = matchProfile.bio ?: "",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}