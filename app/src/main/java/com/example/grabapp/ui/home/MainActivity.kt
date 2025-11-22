package com.example.grabapp.ui.home
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.ActivityMainBinding
import com.example.grabapp.ui.address_selection.AddressSelectionActivity
import com.example.grabapp.ui.address_selection.fragment.DetailPackageFragment
import com.example.grabapp.ui.home.adapter.MainPageAdapter
import com.example.grabapp.ui.user.ActivityUser
import com.facebook.shimmer.Shimmer
import com.google.android.material.tabs.TabLayoutMediator
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityMainBinding> =
        lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<MainViewModel> {
        return lazy { MainViewModel(application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        setIsLightThemeStatusBar(true )
        MapLibre.getInstance(
            this,
            AddressRepository.API_KEY,
            WellKnownTileServer.MapLibre
        )
        initView()
    }

    override fun handleInsets(v: View, insets: Insets) {
        binding.root.setPadding(0,0,0,insets.bottom)
    }
    private fun initView(){
        binding.viewPager.adapter = MainPageAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager){tab,position ->
            when(position){
                MainPageAdapter.FRAGMENT_MAIN->{
                    tab.setIcon(R.drawable.ic_home)
                    tab.setText("Home")
                }
                MainPageAdapter.FRAGMENT_ORDER_HISTORY ->{
                    tab.setIcon(R.drawable.ic_history)
                    tab.setText("History")
                }
            }
        }.attach()
        binding.viewPager.isUserInputEnabled = false

    }

}