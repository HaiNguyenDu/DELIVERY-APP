package com.example.grabapp.driver.home.wallet

import android.os.Bundle
import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.databinding.FragmentWalletBinding
import com.example.grabapp.driver.home.DriverHomeViewModel
import com.example.grabapp.driver.home.wallet.adapter.TransactionHistoryAdapter
import com.example.grabapp.model.TransactionHistory

class WalletFragment : BaseFragment<FragmentWalletBinding, DriverHomeViewModel>() {

    private lateinit var transactionAdapter: TransactionHistoryAdapter
    private var transactions: List<TransactionHistory> = emptyList()

    override fun getLazyBinding(): Lazy<FragmentWalletBinding> =
        lazy { FragmentWalletBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy {
            val fileRepository = FileRepository()
            val aiServiceRepository = AIServiceRepository()
            DriverHomeViewModel(requireActivity().application, fileRepository, aiServiceRepository)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadTransactions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        updateEmptyState()
    }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.emptyView.setPadding(0, inset.top, 0, 0)
        binding.ctlTopBar.setPadding(0, inset.top / 2, 0, 0)
    }

    private fun loadTransactions() {
        transactions = TransactionHistory.getMockTransactions()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionHistoryAdapter(items = transactions)

        binding.rvTransactionHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun updateEmptyState() {
        val hasTransactions = transactions.isNotEmpty()
        binding.emptyLayout.isVisible = !hasTransactions
        binding.rvTransactionHistory.isVisible = hasTransactions
    }
}
