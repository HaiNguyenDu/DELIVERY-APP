package com.example.grabapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.api.RetrofitProvider
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.data.auth.AuthApi
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.data.repository.AuthRepository
import com.example.grabapp.databinding.ActivitySplashBinding
import com.example.grabapp.driver.home.DriverHomeActivity
import com.example.grabapp.driver.login.DriverLoginActivity
import com.example.grabapp.util.JwtDecoder
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
        val notificationOrderId = intent.getStringExtra("notification_order_id")
        // Nếu có notification order, bỏ qua delay để navigate nhanh hơn
        val delayTime = if (!notificationOrderId.isNullOrEmpty()) 500L else 2000L
        
        lifecycleScope.launch {
            delay(delayTime)
            checkTokenAndNavigate()
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
    private suspend fun checkTokenAndNavigate() {
        val tokenStorage = TokenStorage(this)
        val notificationOrderId = intent.getStringExtra("notification_order_id")
        
        val accessToken = tokenStorage.getAccessToken()
        
        if (accessToken.isNullOrEmpty()) {
            navigateToLogin(notificationOrderId)
            return
        }
        
        val isExpired = JwtDecoder.isTokenExpired(accessToken)
        
        if (isExpired) {
            val refreshToken = tokenStorage.getRefreshToken()
            if (refreshToken.isNullOrEmpty()) {
                tokenStorage.clear()
                navigateToLogin(notificationOrderId)
                return
            }
            
            val retrofit = RetrofitProvider.create("https://quickdn.undo.it/")
            val api = retrofit.create(AuthApi::class.java)
            val authRepository = AuthRepository(api, tokenStorage)
            
            when (val result = authRepository.refreshToken()) {
                is AuthRepository.RefreshTokenResult.Success -> {
                    navigateToHome(notificationOrderId)
                }
                is AuthRepository.RefreshTokenResult.Error -> {
                    tokenStorage.clear()
                    navigateToLogin(notificationOrderId)
                }
            }
        } else {
            navigateToHome(notificationOrderId)
        }
    }
    
    private fun navigateToHome(notificationOrderId: String?) {
        val intent = Intent(this@SplashActivity, DriverHomeActivity::class.java).apply {
            if (!notificationOrderId.isNullOrEmpty()) {
                putExtra("notification_order_id", notificationOrderId)
            }
        }
        startActivity(intent)
        overridePendingTransition(
            R.anim.anim_translate_in_right,
            R.anim.anim_translate_out_left
        )
        finish()
    }
    
    private fun navigateToLogin(notificationOrderId: String?) {
        val intent = Intent(this@SplashActivity, DriverLoginActivity::class.java).apply {
            if (!notificationOrderId.isNullOrEmpty()) {
                putExtra("notification_order_id", notificationOrderId)
            }
        }
        startActivity(intent)
        overridePendingTransition(
            R.anim.anim_translate_in_right,
            R.anim.anim_translate_out_left
        )
        finish()
    }
}
