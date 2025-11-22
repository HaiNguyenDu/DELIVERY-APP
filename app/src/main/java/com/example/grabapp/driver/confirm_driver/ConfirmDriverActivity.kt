package com.example.grabapp.driver.confirm_driver

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityConfirmDriverBinding
import com.example.grabapp.driver.confirm_driver.eligibility.DriverEligibilityActivity
import com.example.grabapp.driver.confirm_driver.pager.ConfirmPagerAdapter
import com.example.grabapp.driver.register.RegisterActivity
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity
import kotlinx.coroutines.launch

class ConfirmDriverActivity : BaseActivity<ActivityConfirmDriverBinding, ConfirmDriverViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityConfirmDriverBinding> =
        lazy { ActivityConfirmDriverBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<ConfirmDriverViewModel> =
        lazy {
            ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )[ConfirmDriverViewModel::class.java]
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListeners()
        setupToolBar()
        setupViewPager()
        setupObserve()
    }

    private fun setupListeners() {
        binding.ivPrevious.onClickWithScale {
            navigateToPreviousPage()
        }

        binding.ivNext.onClickWithScale {
            navigateToNextPage()
        }
    }

    private fun setupObserve() {
        lifecycleScope.launch {
            viewModel.navigationEvent.collect { event ->
                when (event) {
                    ConfirmNavigationEvent.NEXT_PAGE -> {
                        navigateToNextPage()
                    }

                    ConfirmNavigationEvent.GO_REGISTER -> {
                        startActivity<RegisterActivity>()
                    }

                    ConfirmNavigationEvent.GO_ELIGIBILITY -> {
                        startActivity<DriverEligibilityActivity>()
                    }
                }
            }
        }

    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
            title = ""
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.tvLogOut.onClickWithScale {
        }
    }

    private fun setupViewPager() {
        val adapter = ConfirmPagerAdapter(this)
        binding.samplePager.adapter = adapter
        binding.samplePager.isUserInputEnabled = true
    }

    private fun navigateToPreviousPage() {
        val currentItem = binding.samplePager.currentItem
        if (currentItem > 0) {
            binding.samplePager.setCurrentItem(currentItem - 1, true)
        }
    }

    private fun navigateToNextPage() {
        val currentItem = binding.samplePager.currentItem
        val totalPages = binding.samplePager.adapter?.itemCount ?: 0
        if (currentItem < totalPages - 1) {
            binding.samplePager.setCurrentItem(currentItem + 1, true)
        }
    }
}
