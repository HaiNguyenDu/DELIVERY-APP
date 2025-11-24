package com.example.grabapp.driver.home

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.databinding.ActivityDriverHomeBinding
import com.example.grabapp.driver.home.data.TabType
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.setPadding

class DriverHomeActivity : BaseActivity<ActivityDriverHomeBinding, DriverHomeViewModel>() {

    companion object {
        private const val CURRENT_TAB = "current_tab"
    }

    private var currentTab: TabType = TabType.HOME

    override fun getLazyBinding(): Lazy<ActivityDriverHomeBinding> =
        lazy { ActivityDriverHomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy {
            val fileRepository = FileRepository()
            val aiServiceRepository = AIServiceRepository()
            DriverHomeViewModel(application, fileRepository, aiServiceRepository)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRootColor(getColor(R.color.bg_color))
        binding.lifecycleOwner = this

        setupListener()
        setupFragment(savedInstanceState)
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
