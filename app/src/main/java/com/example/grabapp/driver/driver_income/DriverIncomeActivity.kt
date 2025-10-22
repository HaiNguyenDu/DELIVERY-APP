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
            }
            llBonus.onClickWithScale {
                switchFragment(DriverBonusFragment())
            }
            llWallet.onClickWithScale {
                switchFragment(DriverWalletFragment())
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}
