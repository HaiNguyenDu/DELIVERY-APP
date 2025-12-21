package com.example.grabapp.ui.home

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.local.AppDatabase
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.ActivityMainBinding
import com.example.grabapp.ui.home.adapter.MainPageAdapter
import com.example.grabapp.ui.order.OrderPlacedDialog
import com.example.grabapp.utils.CurrentOrder
import com.example.grabapp.utils.JwtUtils
import com.example.grabapp.utils.SharedPreferencesUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityMainBinding> =
        lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<MainViewModel> {
        return lazy { MainViewModel(AppDatabase.getInstance(this).userDao(), application) }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        }
    }

    private fun permissionNotification() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun observerData() {
        lifecycleScope.launch {
            CurrentOrder.orderID.collect {
                if (it.isEmpty()) return@collect
                viewModel.loadListOrder()
            }
        }
        lifecycleScope.launch {
            viewModel.listOrder.collect { list ->
                if (list.isEmpty()) return@collect
                list.forEach { order ->
                    order.status?.isTerminal()?.let {
                        if ((!it)) {
                            binding.btnPackage.visibility = View.VISIBLE
                            binding.btnPackage.setOnClickListener {
                                CurrentOrder.setOrderId(order.id)
                                showCurrentOrder()
                            }
                            return@collect
                        }
                    }
                }
                binding.btnPackage.visibility = View.GONE
            }
        }
    }

    private fun showCurrentOrder() {
        val dialog = OrderPlacedDialog()
        viewModel.isLoop = true
        dialog.show(supportFragmentManager, "OrderPlacedDialog")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        setIsLightThemeStatusBar(true)
        MapLibre.getInstance(
            this,
            AddressRepository.API_KEY,
            WellKnownTileServer.MapLibre
        )
        initView()
        observerData()
        permissionNotification()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadListOrder()
        if (CurrentOrder.orderID.value.isNotEmpty()) {
            binding.btnPackage.visibility = View.VISIBLE
            showCurrentOrder()
        }else {
            binding.btnPackage.visibility = View.GONE
        }
    }

    override fun handleInsets(v: View, insets: Insets) {
        binding.root.setPadding(0, 0, 0, insets.bottom)
    }

    private fun initView() {
        binding.btnPackage.visibility = View.GONE
        lifecycleScope.launch {
            val token = FirebaseMessaging.getInstance().token.await()
            viewModel.sendFCM(token)
        }
        binding.viewPager.adapter = MainPageAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                MainPageAdapter.FRAGMENT_MAIN -> {
                    tab.setIcon(R.drawable.ic_home)
                    tab.setText("Home")
                }

                MainPageAdapter.FRAGMENT_ORDER_HISTORY -> {
                    tab.setIcon(R.drawable.ic_history)
                    tab.setText("History")
                }
            }
        }.attach()
        binding.viewPager.isUserInputEnabled = false
    }
}