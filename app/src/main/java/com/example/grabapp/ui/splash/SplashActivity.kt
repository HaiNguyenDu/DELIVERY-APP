package com.example.grabapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.Insets
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivitySplashBinding
import com.example.grabapp.ui.login.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<ActivitySplashBinding, NoViewModel>() {
    override fun getLazyBinding(): Lazy<ActivitySplashBinding> =
        lazy { ActivitySplashBinding.inflate(layoutInflater) }


    override fun getLazyViewModel(): Lazy<NoViewModel> {
        return lazy { NoViewModel() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        handleForNextScreen()
    }

    override fun handleInsets(v: View, insets: Insets) {}

    private fun handleForNextScreen() {
        lifecycleScope.launch {
            delay(3000)
            funShowNextScreen()

        }
    }

    private fun funShowNextScreen() {
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        overridePendingTransition(
            R.anim.anim_translate_in_right,
            R.anim.anim_translate_out_left
        )
        finish()
    }
}
