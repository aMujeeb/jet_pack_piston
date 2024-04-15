package com.mujapps.piston.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.mujapps.piston.view.components.rememberSwipeableCardState
import com.mujapps.piston.view.main.MainViewModel
import kotlinx.coroutines.launch

/*@Composable
fun SwipeCards() {
    //TransparentSystemBars()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xfff68084),
                        Color(0xffa6c0fe),
                    )
                )
            )
        //                        .systemBarsPadding()
    ) {
        Box {
            val states = profiles.reversed()
                .map { it to rememberSwipeableCardState() }

            var hint by remember {
                mutableStateOf("Swipe a card or press a button below")
            }

            Hint(hint)

            val scope = rememberCoroutineScope()
            Box(
                Modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .align(Alignment.Center)
            ) {
                states.forEach { (matchProfile, state) ->
                    if (state.swipedDirection == null) {
                        ProfileCard(
                            modifier = Modifier
                                .fillMaxSize()
                                .swipableCard(
                                    state = state,
                                    blockedDirections = listOf(Down),
                                    onSwiped = {
                                        // swipes are handled by the LaunchedEffect
                                        // so that we track button clicks & swipes
                                        // from the same place
                                    },
                                    onSwipeCancel = {
                                        //Log.d("Swipeable-Card", "Cancelled swipe")
                                        hint = "You canceled the swipe"
                                    }
                                ),
                            matchProfile = matchProfile
                        )
                    }
                    LaunchedEffect(matchProfile, state.swipedDirection) {
                        if (state.swipedDirection != null) {
                            hint = "You swiped ${state.swipedDirection!!}"
                        }
                    }
                }
            }
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircleButton(
                    onClick = {
                        scope.launch {
                            val last = states.reversed()
                                .firstOrNull {
                                    it.second.offset.value == Offset(0f, 0f)
                                }?.second
                            last?.swipe(Left)
                        }
                    },
                    icon = Icons.Rounded.Close
                )
                CircleButton(
                    onClick = {
                        scope.launch {
                            val last = states.reversed()
                                .firstOrNull {
                                    it.second.offset.value == Offset(0f, 0f)
                                }?.second

                            last?.swipe(Right)
                        }
                    },
                    icon = Icons.Rounded.Favorite
                )
            }
        }
    }
}*/


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
    if (inProgressState) {
        Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
            CommonProgressSpinner()
        }
    } else {
        val profiles = mMainViewModel.mMatchProfilesState.collectAsStateWithLifecycle().value

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xfff68084), Color(0xffa6c0fe))))
        ) {
            //Spacer
            Spacer(modifier = Modifier.height(4.dp))
            //Cards
            //val states = profiles.map { it to rememberSwipeableCardState() }

            val showLeftSwipe = rememberSaveable {
                mutableStateOf(false)
            }

            val showRightSwipe = rememberSaveable {
                mutableStateOf(false)
            }

            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxHeight(0.8f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "No Profiles Available")
                }

                profiles?.forEach { matchProfile ->

                    SwipeCard(onSwipeLeft = {
                        //Dislike Functionality
                        LoggerUtils.logMessage("To Left Most")
                        mMainViewModel.onSwiped(matchProfile.userId)
                    }, onSwipeRight = {
                        //like Functionality
                        LoggerUtils.logMessage("To Right Most")
                        mMainViewModel.onSwiped(matchProfile.userId)
                    }, onSwipeIntermediateLeft = {
                        showLeftSwipe.value = true
                        showRightSwipe.value = false
                    }, onSwipeIntermediateRight = {
                        showRightSwipe.value = true
                        showLeftSwipe.value = false
                    }) {
                        ProfileSelector(matchProfile, showLeftSwipe, showRightSwipe)
                        val scope = rememberCoroutineScope()
                        Row(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Image(painter = painterResource(id = R.drawable.baseline_cancel), contentDescription = "Reject", modifier = Modifier.clickable {
                                scope.launch {
                                    //Reject functionality
                                    //mMainViewModel.onDisLike(matchProfile)
                                    showLeftSwipe.value = true
                                }
                            })


                            Image(painter = painterResource(id = R.drawable.baseline_done), contentDescription = "Reject", modifier = Modifier.clickable {
                                scope.launch {
                                    //Accept functionality
                                    // mMainViewModel.onLike(matchProfile)
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
    /*Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xfff68084), Color(0xffa6c0fe))))
    ) {
        //profiles.forEach {

        val showLeftSwipe = rememberSaveable {
            mutableStateOf(false)
        }

        val showRightSwipe = rememberSaveable {
            mutableStateOf(false)
        }

        SwipeCard(onSwipeLeft = {
            //Dislike Functionality
            LoggerUtils.logMessage("To Left Most")
        }, onSwipeRight = {
            //like Functionality
            LoggerUtils.logMessage("To Right Most")
        }, onSwipeIntermediateLeft = {
            LoggerUtils.logMessage("To Left")
            showLeftSwipe.value = true
            showRightSwipe.value = false
        }, onSwipeIntermediateRight = {
            LoggerUtils.logMessage("To Right")
            showRightSwipe.value = true
            showLeftSwipe.value = false
        }) {
            //ProfileSelector(it.name, it.drawableResId, showLeftSwipe, showRightSwipe)
            ProfileSelector(profiles[0].name, profiles[0].drawableResId, showLeftSwipe, showRightSwipe)
        }
        //}
        BottomNavigationMenu(selectedItem = BottomNavigationItem.SWIPE, navController = navController)
    }*/
}

@Composable
fun ProfileSelector(matchProfile: UserData, showLeftSwipe: MutableState<Boolean>, showRightSwipe: MutableState<Boolean>) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
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
            Text(
                text = matchProfile.name ?: matchProfile.userName ?: "",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            )
            Text(
                text = matchProfile.bio ?: "",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White),
                modifier = Modifier.padding(16.dp)
            )
           /* if (showLeftSwipe.value) {
                Text(text = "Cat Left", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White))
            }
            if (showRightSwipe.value) {
                Text(text = "Cat Right", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White))
            }*/
        }
    }
}