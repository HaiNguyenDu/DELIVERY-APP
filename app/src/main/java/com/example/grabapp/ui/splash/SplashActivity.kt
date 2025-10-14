package com.example.grabapp.ui.splash

import android.annotation.SuppressLint
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
import com.example.grabapp.common.TOPIC_FCM
import com.example.grabapp.databinding.ActivitySplashBinding
import com.example.grabapp.service.MyFirebaseMessagingService
import com.example.grabapp.ui.home.MainActivity
import com.example.grabapp.ui.login.LoginActivity
import com.example.grabapp.ui.user.ActivityUser
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
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
        permissionNotification()
        handleFCM()
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

    private fun handleFCM() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_FCM)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TOPIC_FCM", "success")
                } else {
                    Log.w("TOPIC_FCM", "fail", task.exception)
                }
            }
    }

    private fun handleForNextScreen() {
        lifecycleScope.launch {
            delay(4000)
            funShowNextScreen()
        }
    }

    private fun setUpUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.statusBars())
    }

    private fun permissionNotification() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                requestPermissionLaucher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun funShowNextScreen() {
        if (!::intentActivity.isInitialized)
            intentActivity = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intentActivity)
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
