package com.example.grabapp.model

import androidx.annotation.StringRes
import com.example.grabapp.R

enum class Transportation(
    val transportationName: String,
    @StringRes val stringResId: Int,
) {
    GRAB_BIKE("GrabBike (2 bánh)", R.string.grab_bike),
    GRAB_CAR("GrabCar", R.string.grab_car),
    GRAB_TAXI("GrabTaxi", R.string.grab_taxi)
}
