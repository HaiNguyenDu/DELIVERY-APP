package com.example.grabapp.ui.home
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.ActivityMainBinding
import com.example.grabapp.ui.address_selection.AddressSelectionActivity
import com.example.grabapp.ui.address_selection.fragment.DetailPackageFragment
import com.example.grabapp.ui.user.ActivityUser
import com.facebook.shimmer.Shimmer
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        val fineLocationPermission = permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationPermission =
            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationPermission && coarseLocationPermission) {
            startActivity(Intent(this, AddressSelectionActivity::class.java))
            overridePendingTransition(
                R.anim.anim_translate_in_right,
                R.anim.anim_translate_out_left
            )
        }
    }

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
        MapLibre.getInstance(
            this,
            AddressRepository.API_KEY,
            WellKnownTileServer.MapLibre
        )
    }

    fun shimmer() {
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
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                startActivity(Intent(this, AddressSelectionActivity::class.java))
                overridePendingTransition(
                    R.anim.anim_translate_in_right,
                    R.anim.anim_translate_out_left
                )
            }
        }
    }

}