package com.example.grabapp.base

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.grabapp.R
import com.example.grabapp.extention.setPadding
import com.example.grabapp.ui.login.LoginActivity
import com.example.grabapp.utils.SessionManager
import kotlinx.coroutines.launch

abstract class BaseActivity<T : ViewBinding, V : BaseViewModel> : AppCompatActivity() {
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
        observerTokenExpired()
    }

    private fun observerTokenExpired(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                SessionManager.logoutEvent.collect {
                    onTokenExpired()
                }
            }
        }
    }

    private fun onTokenExpired(){
        if (this is LoginActivity)
            return
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
        window.decorView.setBackgroundColor(getColor(R.color.white))
    }

    open fun handleInsets(v: View, insets: Insets) {
        v.setPadding(insets)
    }

    protected fun setRootColor(color: Int) {
        window.decorView.setBackgroundColor(color)
    }
}