package com.example.grabapp.driver.register.bottom_sheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.IBottomSheetDialogFragment
import com.example.grabapp.databinding.BottomSheetBankBinding
import com.example.grabapp.driver.register.adapter.BankAdapter
import com.example.grabapp.model.Bank

class BankBottomSheet(
    private val onBankSelected: (String) -> Unit
) : IBottomSheetDialogFragment<BottomSheetBankBinding>(
    BottomSheetBankBinding::inflate
) {

    override val TAG: String = "BankBottomSheet"

    private lateinit var adapter: BankAdapter

    override fun initViews() {
        setupRecyclerView()
    }

    override fun initObservers() {
    }

    override fun initListeners() {
    }

    private fun setupRecyclerView() {
        val bankList = Bank.entries.toList()

        adapter = BankAdapter(
            items = bankList,
            onItemClick = { bankName ->
                onBankSelected(bankName)
                dismiss()
            }
        )

        viewBinding.rvBank.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BankBottomSheet.adapter
        }
    }
}

