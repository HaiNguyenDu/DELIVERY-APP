package com.example.grabapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.Insets
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.ActivitySplashBinding
import com.example.grabapp.ui.login.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class SplashActivity : BaseActivity<ActivitySplashBinding, NoViewModel>() {
    override fun getLazyBinding(): Lazy<ActivitySplashBinding> =
        lazy { ActivitySplashBinding.inflate(layoutInflater) }


    override fun getLazyViewModel(): Lazy<NoViewModel> {
        return lazy { NoViewModel(application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpUi()
        handleForNextScreen()
    }

    override fun handleInsets(v: View, insets: Insets) {}

    private fun handleForNextScreen() {
        lifecycleScope.launch {
            delay(3000)
            funShowNextScreen()

        }
    }

    private fun setUpUi(){
        WindowCompat.setDecorFitsSystemWindows(window,false)
        val controller = WindowCompat.getInsetsController(window,window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.statusBars())

        MapLibre.getInstance(
            this,
            AddressRepository.API_KEY,
            WellKnownTileServer.MapLibre
        )
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
