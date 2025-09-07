package com.example.grabapp.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.example.grabapp.extention.setPadding

open class BaseActivity<T : ViewBinding, V : BaseViewModel> : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        handleInsets()
        setStatusBarColor()
    }

    private fun handleInsets() {
        val root = window.decorView.rootView
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val inset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            handleInsets(v, inset)
            WindowInsetsCompat.CONSUMED
        }
    }

    protected fun setStatusBarColor() {
        val controller = WindowCompat.getInsetsController(window, window.decorView.rootView)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true
        window.decorView.setBackgroundColor(Color.WHITE)
    }

    open fun handleInsets(v: View, insets: Insets) {
        v.setPadding(insets)
    }
}