package com.mujapps.piston.view.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mujapps.piston.utils.LoggerUtils
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SwipeCard(
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    onSwipeIntermediateLeft: () -> Unit = {},
    onSwipeIntermediateRight: () -> Unit = {},
    swipeThreshold: Float = 240f,
    sensitivityFactor: Float = 2f,
    content: @Composable () -> Unit
) {
    var offset by remember { mutableFloatStateOf(0f) }
    var dismissRight by remember { mutableStateOf(false) }
    var dismissLeft by remember { mutableStateOf(false) }
    val density = LocalDensity.current.density


    //Intermediate States
    var dismissIntermediateRight by remember { mutableStateOf(false) }
    var dismissIntermediateLeft by remember { mutableStateOf(false) }

    LaunchedEffect(dismissRight) {
        if (dismissRight) {
            delay(500)
            onSwipeRight.invoke()
            dismissRight = false
        }
    }

    LaunchedEffect(dismissLeft) {
        if (dismissLeft) {
            delay(500)
            onSwipeLeft.invoke()
            dismissLeft = false
        }
    }

    LaunchedEffect(dismissIntermediateRight) {
        if (dismissIntermediateRight) {
            delay(500)
            onSwipeIntermediateRight.invoke()
            dismissIntermediateRight = false
        }
    }

    LaunchedEffect(dismissIntermediateLeft) {
        if (dismissIntermediateLeft) {
            delay(500)
            onSwipeIntermediateLeft.invoke()
            dismissIntermediateLeft = false
        }
    }

    Box(modifier = Modifier
        .offset { IntOffset(offset.roundToInt(), 0) }
        .pointerInput(Unit) {
            detectHorizontalDragGestures(onDragEnd = {
                offset = 0f
            }) { change, dragAmount ->

                offset += (dragAmount / density) * sensitivityFactor

                when {

                    offset > swipeThreshold -> {
                        dismissRight = true
                    }

                    offset < -swipeThreshold -> {
                        dismissLeft = true
                    }

                    //Intermediate Scenarios**********
                    offset > (swipeThreshold - 120) -> {
                        dismissIntermediateRight = true
                    }

                    offset < (-swipeThreshold + 120) -> {
                        dismissIntermediateLeft = true
                    }

                    //******************************
                    else -> {
                        dismissRight = false
                        dismissLeft = false

                        dismissIntermediateRight = false
                        dismissIntermediateLeft = false
                    }

                }
                if (change.positionChange() != Offset.Zero) change.consume()
            }
        }
        .graphicsLayer(
            alpha = 10f - animateFloatAsState(if (dismissRight) 1f else 0f, label = "").value,
            rotationZ = animateFloatAsState(offset / 50, label = "").value
        )) {
        content()
    }
}