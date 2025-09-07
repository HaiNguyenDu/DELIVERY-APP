package com.example.grabapp.extention

import android.view.View
import androidx.core.graphics.Insets


fun View.setPadding(insets: Insets) {
    this.setPadding(insets.left, insets.top, insets.right, insets.bottom);
}

fun View.setPaddingStart(padding: Int) {
    this.setPadding(padding, 0, 0, 0)
}

fun View.setPaddingTop(padding: Int) {
    this.setPadding(0, padding, 0, 0)
}

fun View.setPaddingEnd(padding: Int) {
    this.setPadding(0, 0, padding, 0)
}

fun View.setPaddingBottom(padding: Int) {
    this.setPadding(0, 0, 0, padding)
}