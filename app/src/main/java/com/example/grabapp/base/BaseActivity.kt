package com.example.grabapp.base

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.example.grabapp.extention.setPadding

abstract  class BaseActivity<T : ViewBinding, V : BaseViewModel> : AppCompatActivity() {
    abstract fun getLazyBinding(): Lazy<T>
    abstract fun getLazyViewModel(): Lazy<V>
    protected val binding: T by this.getLazyBinding()
    protected val viewModel: V by this.getLazyViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setIsLightThemeStatusBar()
        setContentView(binding.root)
        handleInsets()

    }

    private fun handleInsets() {
        val root = window.decorView.rootView
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val inset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            handleInsets(v, inset)
            insets
        }
    }

    protected fun setIsLightThemeStatusBar(value: Boolean = true) {
        val controller = WindowCompat.getInsetsController(window, window.decorView.rootView)
        controller.isAppearanceLightStatusBars = value
        controller.isAppearanceLightNavigationBars = value
        window.decorView.setBackgroundColor("#F8F8F8".toColorInt())
    }

    open fun handleInsets(v: View, insets: Insets) {
        v.setPadding(insets)
    }

}