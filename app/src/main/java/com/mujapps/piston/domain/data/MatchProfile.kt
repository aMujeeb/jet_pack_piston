package com.mujapps.piston.domain.data

import androidx.annotation.DrawableRes

data class MatchProfile(
    val name: String,
    @DrawableRes val drawableResId: Int
)
