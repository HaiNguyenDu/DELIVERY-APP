package com.example.grabapp.driver.register.data

import android.graphics.Bitmap

data class VehicleConfirmData(
    var frontSideBitmap: Bitmap? = null,
    var backSideBitmap: Bitmap? = null,
    var rightSideBitmap: Bitmap? = null,
    var leftSideBitmap: Bitmap? = null
)
