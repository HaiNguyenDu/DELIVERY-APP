package com.example.grabapp.driver.driver_income.period_income

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityPeriodIncomeBinding
import com.example.grabapp.model.IncomeHistory

class PeriodIncomeActivity : BaseActivity<ActivityPeriodIncomeBinding, PeriodIncomeViewModel>() {

    private var selectedPeriodIncomeType: String = "Thu nhập hôm nay"
    private var selectedIncome: Int = 100000
    private val mockIncomeHistory = mockIncomeHistory()

    override fun getLazyBinding(): Lazy<ActivityPeriodIncomeBinding> =
        lazy { ActivityPeriodIncomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<PeriodIncomeViewModel> {
        return lazy { PeriodIncomeViewModel(application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSelectedPeriodIncome()
        setupToolbar()
        setupIncomeData()
        setupRecyclerView()
    }

    private fun getSelectedPeriodIncome() {
        selectedPeriodIncomeType = intent.getStringExtra("periodIncomeType") ?: "Thu nhập hôm nay"
        selectedIncome = intent.getIntExtra("income", 100000)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.title = selectedPeriodIncomeType
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back)
        }
        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun setupIncomeData() {
        binding.apply {
            tvIncome.text = "$selectedIncome VNĐ"
            tvJobCount.text = "${mockIncomeHistory.size} jobs"
        }
    }

    private fun setupRecyclerView() {
        val incomeHistoryAdapter = IncomeHistoryAdapter(mockIncomeHistory)
        binding.rvDetailIncome.apply {
            layoutManager = LinearLayoutManager(this@PeriodIncomeActivity)
            adapter = incomeHistoryAdapter
        }
    }

    private fun mockIncomeHistory(): List<IncomeHistory> {
        return listOf(
            IncomeHistory("14:06 2025 - 14:24 AM", 60000),
            IncomeHistory("13:45 2025 - 14:02 AM", 75000),
            IncomeHistory("13:20 2025 - 13:38 AM", 55000),
            IncomeHistory("12:55 2025 - 13:15 AM", 80000),
            IncomeHistory("12:30 2025 - 12:48 AM", 65000),
            IncomeHistory("12:05 2025 - 12:25 AM", 70000),
            IncomeHistory("11:40 2025 - 11:58 AM", 58000),
            IncomeHistory("11:15 2025 - 11:35 AM", 72000),
            IncomeHistory("10:50 2025 - 11:10 AM", 68000),
            IncomeHistory("10:25 2025 - 10:45 AM", 63000)
        )
    }
}
