package com.example.grabapp.driver.driver_income.income

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentIncomeBinding
import com.example.grabapp.driver.driver_income.DriverIncomeViewModel
import com.example.grabapp.driver.driver_income.period_income.PeriodIncomeActivity
import com.example.grabapp.model.PeriodIncome

class IncomeFragment : BaseFragment<FragmentIncomeBinding, DriverIncomeViewModel>() {

    override fun getLazyBinding(): Lazy<FragmentIncomeBinding> =
        lazy { FragmentIncomeBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverIncomeViewModel> =
        lazy { DriverIncomeViewModel(requireActivity().application) }

    override fun setUpClick() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val incomeAdapter = IncomeAdapter(
            items = PeriodIncome.entries,
            onItemClick = {
                navigateToPeriodIncome(it)
            }
        )

        binding.rvIncome.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = incomeAdapter
        }
    }

    private fun navigateToPeriodIncome(periodIncome: PeriodIncome) {
        val intent =
            Intent(requireContext(), PeriodIncomeActivity::class.java)
        intent.putExtra("periodIncomeType", periodIncome.periodIncomeType)
        intent.putExtra("income", periodIncome.income)
        startActivity(intent)
    }
}
