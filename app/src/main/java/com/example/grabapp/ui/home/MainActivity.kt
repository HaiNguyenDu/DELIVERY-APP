package com.example.grabapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityMainBinding
import com.example.grabapp.ui.address_selection.AddressSelectionActivity
import com.example.grabapp.ui.user.ActivityUser
import com.facebook.shimmer.Shimmer

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityMainBinding> =
        lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<MainViewModel> {
        return lazy { MainViewModel(application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        observeView()
        shimmer()
        setIsLightThemeStatusBar(false)
    }

    fun shimmer(){
        val shimmerBuilder = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(1f)
            .setHighlightAlpha(0.08f)
            .setTilt(30f)
            .setDropoff(0.5f)
            .setDuration(1500L)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setRepeatCount(0)
            .setShape(Shimmer.Shape.LINEAR)

        binding.shimmer.setShimmer(shimmerBuilder.build())
        val handler = Handler(Looper.getMainLooper())
        val shimmerRunnable = object : Runnable {
            override fun run() {
                binding.shimmer.setShimmer(shimmerBuilder.build())
                binding.shimmer.startShimmer()
                handler.postDelayed({
                    binding.shimmer.stopShimmer()
                }, 1200L)
                handler.postDelayed(this, 6000L)
            }
        }

        handler.postDelayed(shimmerRunnable, 2000L)
    }

    override fun handleInsets(v: View, insets: Insets) {
        v.setPadding(insets.left, 0, insets.right, insets.bottom)
        binding.layoutHeader.setPadding(
            binding.layoutHeader.paddingLeft,
            insets.top,
            binding.layoutHeader.paddingEnd,
            0
        )
    }

    private fun observeView() {
        binding.btnUser.setOnClickListener {
            startActivity(Intent(this, ActivityUser::class.java))
            overridePendingTransition(
                R.anim.anim_translate_in_right,
                R.anim.anim_translate_out_left
            )
        }
        binding.btnShipping.setOnClickListener {
            startActivity(Intent(this, AddressSelectionActivity::class.java))
            overridePendingTransition(
                R.anim.anim_translate_in_right,
                R.anim.anim_translate_out_left
            )
        }
    }

}