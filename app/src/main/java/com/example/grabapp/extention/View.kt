package com.example.grabapp.extention

import android.annotation.SuppressLint
import android.view.MotionEvent
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

@SuppressLint("ClickableViewAccessibility")
fun View.onClickWithScale(
    debounceTime: Long = 300L,
    onClick: () -> Unit
) {
    var lastClickTime = 0L

    setOnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start()
            }

            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction {
                    if (now - lastClickTime >= debounceTime) {
                        lastClickTime = now
                        onClick()
                    }
                }.start()
            }

            MotionEvent.ACTION_CANCEL -> {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
        }
        true
    }
}
