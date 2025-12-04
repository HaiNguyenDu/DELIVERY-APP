package com.example.grabapp.driver.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.databinding.ActivityDriverHomeBinding
import com.example.grabapp.driver.home.data.TabType
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.service.MyFirebaseMessagingService

class DriverHomeActivity : BaseActivity<ActivityDriverHomeBinding, DriverHomeViewModel>() {

    companion object {
        private const val CURRENT_TAB = "current_tab"
    }

    private var currentTab: TabType = TabType.HOME
    
    private val fileRepository by lazy { FileRepository() }
    private val aiServiceRepository by lazy { AIServiceRepository() }
    private val orderRepository by lazy { OrderRepository(this) }
    private val viewModelFactory by lazy {
        DriverHomeViewModelFactory(application, fileRepository, aiServiceRepository, orderRepository)
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
            } else {
            }
        }
    
    private val newOrderReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("DriverHomeActivity", "=== Nhận broadcast ===")
            Log.d("DriverHomeActivity", "Intent action: ${intent?.action}")
            Log.d("DriverHomeActivity", "Expected action: ${MyFirebaseMessagingService.ACTION_NEW_ORDER}")
            
            if (intent?.action == MyFirebaseMessagingService.ACTION_NEW_ORDER) {
                val orderId = intent.getStringExtra(MyFirebaseMessagingService.EXTRA_ORDER_ID)
                Log.d("DriverHomeActivity", "OrderID từ broadcast: $orderId")
                orderId?.let {
                    handleNewOrderNotification(it)
                } ?: run {
                    Log.e("DriverHomeActivity", "OrderID là null!")
                }
            } else {
                Log.w("DriverHomeActivity", "Action không khớp! Intent: $intent")
            }
        }
    }

    override fun getLazyBinding(): Lazy<ActivityDriverHomeBinding> =
        lazy { ActivityDriverHomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { ViewModelProvider(this, viewModelFactory)[DriverHomeViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRootColor(getColor(R.color.bg_color))
        binding.lifecycleOwner = this


        //requestNotificationPermissionIfNeeded()

        setupListener()
        setupFragment(savedInstanceState)
        fetchOrders()
        registerNewOrderReceiver()
        
        // Khởi động location updates nếu connection state là CONNECTED
        viewModel.startLocationUpdatesIfConnected()
    }
    
    override fun onResume() {
        super.onResume()
        // Đảm bảo location updates được start khi resume nếu connection state là CONNECTED
        viewModel.startLocationUpdatesIfConnected()
    }

//    private fun requestNotificationPermissionIfNeeded() {
//        if (!NotificationPermissionHelper.hasNotificationPermission(this)) {
//            NotificationPermissionHelper.requestNotificationPermission(requestNotificationPermissionLauncher)
//        }
//    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(newOrderReceiver)
        if (!isChangingConfigurations) {
            viewModel.handleAppKilled()
        }
    }
    
    private fun fetchOrders() {
        viewModel.fetchOrders()
    }

    private fun setupListener() {
        binding.apply {
            llHome.onClickWithScale { navigateToTab(TabType.HOME) }
            llHistory.onClickWithScale { navigateToTab(TabType.HISTORY) }
            llMessage.onClickWithScale { navigateToTab(TabType.MESSAGE) }
            llWallet.onClickWithScale { navigateToTab(TabType.WALLET) }
            llProfile.onClickWithScale { navigateToTab(TabType.PROFILE) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_TAB, currentTab.ordinal)
    }

    override fun handleInsets(v: View, insets: Insets) {
        binding.fragmentContainer.setPadding(0, -insets.top, 0, 0)
        binding.llBottomBar.setPadding(0, 0, 0, insets.bottom)
    }

    private fun setupFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            navigateToTab(TabType.HOME)
        } else {
            val savedTabOrdinal = savedInstanceState.getInt(CURRENT_TAB, TabType.HOME.ordinal)
            currentTab = TabType.entries.toTypedArray().getOrElse(savedTabOrdinal) { TabType.HOME }

            binding.selectedTab = currentTab
        }
    }

    private fun navigateToTab(tabType: TabType) {
        if (shouldSkipNavigation(tabType)) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, tabType.subFragment())
            .commit()
        currentTab = tabType

        binding.selectedTab = currentTab
    }

    private fun shouldSkipNavigation(tabType: TabType): Boolean {
        val existingFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        return currentTab == tabType && existingFragment != null
    }
    
    private fun registerNewOrderReceiver() {
        val filter = IntentFilter(MyFirebaseMessagingService.ACTION_NEW_ORDER)
        Log.d("DriverHomeActivity", "Đăng ký BroadcastReceiver với action: ${MyFirebaseMessagingService.ACTION_NEW_ORDER}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // RECEIVER_NOT_EXPORTED = 2 (API 33+)
            registerReceiver(newOrderReceiver, filter, 2)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(newOrderReceiver, filter)
        }
        Log.d("DriverHomeActivity", "BroadcastReceiver đã được đăng ký")
    }
    
    private fun handleNewOrderNotification(orderId: String) {
        Log.d("DriverHomeActivity", "=== Xử lý notification ===")
        Log.d("DriverHomeActivity", "OrderID: $orderId")
        Log.d("DriverHomeActivity", "Current tab: $currentTab")
        Log.d("DriverHomeActivity", "Tab HOME: ${TabType.HOME}")
        
        // Chỉ hiển thị dialog nếu đang ở tab HOME và đang online
        if (currentTab == TabType.HOME) {
            Log.d("DriverHomeActivity", "Đang ở tab HOME, tìm HomeFragment")
            val homeFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as? HomeFragment
            if (homeFragment != null) {
                Log.d("DriverHomeActivity", "HomeFragment tìm thấy, gọi showNewOrderDialogByOrderId")
                homeFragment.showNewOrderDialogByOrderId(orderId)
            } else {
                Log.e("DriverHomeActivity", "HomeFragment không tìm thấy!")
            }
        } else {
            Log.w("DriverHomeActivity", "Không ở tab HOME, currentTab: $currentTab")
        }
    }
}
