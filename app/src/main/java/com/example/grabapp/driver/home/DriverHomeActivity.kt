package com.example.grabapp.driver.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.data.OrderStorage
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.databinding.ActivityDriverHomeBinding
import com.example.grabapp.driver.base.BaseDriverActivity
import com.example.grabapp.driver.home.data.TabType
import com.example.grabapp.driver.order_detail.OrderDetailActivity
import com.example.grabapp.extention.onClickWithScale
import kotlinx.coroutines.launch

class DriverHomeActivity : BaseDriverActivity<ActivityDriverHomeBinding, DriverHomeViewModel>() {

    companion object {
        private const val CURRENT_TAB = "current_tab"
    }

    private var currentTab: TabType = TabType.HOME
    private lateinit var orderStorage: OrderStorage
    
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

    override fun getLazyBinding(): Lazy<ActivityDriverHomeBinding> =
        lazy { ActivityDriverHomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { ViewModelProvider(this, viewModelFactory)[DriverHomeViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderStorage = OrderStorage(this)
        setRootColor(getColor(R.color.bg_color))
        binding.lifecycleOwner = this


        //requestNotificationPermissionIfNeeded()

        setupListener()
        setupFragment(savedInstanceState)
        fetchOrders()
        observeActiveOrder()
        
        // Khởi động location updates nếu connection state là CONNECTED
        viewModel.startLocationUpdatesIfConnected()
    }
    
    private fun observeActiveOrder() {
        binding.lottieCurrentOrder.onClickWithScale {
            val orderId = orderStorage.getActiveOrderId()
            if (!orderId.isNullOrEmpty()) {
                viewModel.fetchOrderById(orderId) { order ->
                    if (order != null) {
                        navigateToOrderDetail(order)
                    }
                }
            }
        }
        updateActiveOrderVisibility()
    }
    
    private fun navigateToOrderDetail(order: com.example.grabapp.model.Order) {
        val intent = Intent(this, OrderDetailActivity::class.java).apply {
            putExtra("extra_order", order)
        }
        startActivity(intent)
    }
    
    override fun onResume() {
        super.onResume()
        // Đảm bảo location updates được start khi resume nếu connection state là CONNECTED
        viewModel.startLocationUpdatesIfConnected()
        updateActiveOrderVisibility()
    }
    
    private fun updateActiveOrderVisibility() {
        val hasActiveOrder = orderStorage.hasActiveOrder()
        binding.lottieCurrentOrder.visibility = if (hasActiveOrder) View.VISIBLE else View.GONE
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
}
