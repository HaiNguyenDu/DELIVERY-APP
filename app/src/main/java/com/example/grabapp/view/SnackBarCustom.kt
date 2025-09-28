package com.example.grabapp.view

import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class SnackBarCustom(
    private val view: View,
    private val message: String,
    private val backgroundColor: Int = Color.BLACK,
    private val textColor: Int = Color.WHITE,
    private val bottomMarginDp: Float = 50f
) {
    fun show() {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view
        snackbarView.background?.setTint(backgroundColor)

        val textView =
            snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(textColor)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            bottomMarginDp,
            view.resources.displayMetrics
        )
        snackbarView.translationY = snackbarView.height.toFloat()
        snackbar.show()
        snackbarView.animate()
            .translationY(-marginInPx)
            .setDuration(300)
            .start()
    }
}