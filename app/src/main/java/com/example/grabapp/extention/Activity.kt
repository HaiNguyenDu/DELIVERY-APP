package com.example.grabapp.extention

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun Activity.hideKeyboard(view: View){
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken,0)
}

inline fun <reified A : Activity> Context.startActivity(
    configExtras: Intent.() -> Unit = {},
) {
    ContextCompat.startActivity(
        this@startActivity,
        Intent(this@startActivity, A::class.java).apply(configExtras),
        null
    )
}
