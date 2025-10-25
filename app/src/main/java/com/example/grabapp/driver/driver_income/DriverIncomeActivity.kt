package com.example.grabapp.driver.driver_income

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverIncomeBinding
import com.example.grabapp.driver.driver_income.bonus.DriverBonusFragment
import com.example.grabapp.driver.driver_income.income.IncomeFragment
import com.example.grabapp.driver.driver_income.wallet.DriverWalletFragment
import com.example.grabapp.extention.onClickWithScale

class DriverIncomeActivity : BaseActivity<ActivityDriverIncomeBinding, DriverIncomeViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverIncomeBinding> =
        lazy { ActivityDriverIncomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverIncomeViewModel> {
        return lazy { DriverIncomeViewModel(application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
        setupFragmentContainer()
        observeView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setTitle(R.string.income)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupFragmentContainer() {
        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, IncomeFragment())
                .commit()
        }
    }

    private fun observeView() {
        binding.apply {
            llIncome.onClickWithScale {
                switchFragment(IncomeFragment())
                updateBottomBarSelection(0)
            }
            llBonus.onClickWithScale {
                switchFragment(DriverBonusFragment())
                updateBottomBarSelection(1)
            }
            llWallet.onClickWithScale {
                switchFragment(DriverWalletFragment())
                updateBottomBarSelection(2)
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateBottomBarSelection(selectedIndex: Int) {
        binding.apply {
            resetBottomBarColors()
            
            when (selectedIndex) {
                0 -> {
                    ivIncome.setColorFilter(getColor(R.color.green))
                    tvIncome.setTextColor(getColor(R.color.green))
                }
                1 -> {
                    ivBonus.setColorFilter(getColor(R.color.green))
                    tvBonus.setTextColor(getColor(R.color.green))
                }
                2 -> {
                    ivWallet.setColorFilter(getColor(R.color.green))
                    tvWallet.setTextColor(getColor(R.color.green))
                }
            }
        }
    }

    private fun resetBottomBarColors() {
        binding.apply {
            ivIncome.setColorFilter(getColor(R.color.black))
            tvIncome.setTextColor(getColor(R.color.black))
            ivBonus.setColorFilter(getColor(R.color.black))
            tvBonus.setTextColor(getColor(R.color.black))
            ivWallet.setColorFilter(getColor(R.color.black))
            tvWallet.setTextColor(getColor(R.color.black))
        }
    }

}
