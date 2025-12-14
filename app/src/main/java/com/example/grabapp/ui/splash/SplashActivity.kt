package com.example.grabapp.ui.splash

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.ActivitySplashBinding
import com.example.grabapp.service.MyFirebaseMessagingService
import com.example.grabapp.ui.home.MainActivity
import com.example.grabapp.ui.login.LoginActivity
import com.example.grabapp.ui.login.LoginViewModel
import com.example.grabapp.ui.user.ActivityUser
import com.example.grabapp.utils.SharedPreferencesUtils
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class SplashActivity : BaseActivity<ActivitySplashBinding, NoViewModel>() {
    private lateinit var intentActivity: Intent
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
        handleIntent()
    }

    override fun handleInsets(v: View, insets: Insets) {}

    private fun handleIntent() {
        val activity = intent.getStringExtra(MyFirebaseMessagingService.Companion.FCM_ACTIVITY)
        val data = intent.getStringExtra(MyFirebaseMessagingService.Companion.FCM_DATA)
        try {
            val nextActivity: Class<*> = Class.forName(activity)
            intentActivity = Intent(this, nextActivity)
            when (nextActivity) {
                MainActivity::class.java -> {
                    intentActivity.putExtra("test", data)
                }

                ActivityUser::class.java -> {
                    intentActivity.putExtra("userName", data)
                }
            }
        } catch (e: Exception) {
            Log.e("Splash", "invalid activity")
            return
        }
    }

    private fun handleForNextScreen() {
        lifecycleScope.launch {
            delay(3000)
            funShowNextScreen()

        }
    }

    private fun setUpUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.statusBars())

        MapLibre.getInstance(
            this,
            AddressRepository.API_KEY,
            WellKnownTileServer.MapLibre
        )
    }

    private fun funShowNextScreen() {
        val token = SharedPreferencesUtils(this).getToken()
        val newActivity = if (token.isNotEmpty()) MainActivity::class.java
        else LoginActivity::class.java
        startActivity(Intent(this@SplashActivity, newActivity))
        overridePendingTransition(
            R.anim.anim_translate_in_right,
            R.anim.anim_translate_out_left
        )
        finish()
    }

    private val requestPermissionLaucher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        }
    }
}
