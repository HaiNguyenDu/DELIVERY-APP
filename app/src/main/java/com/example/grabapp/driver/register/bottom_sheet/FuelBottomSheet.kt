package com.example.grabapp.driver.register.bottom_sheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.IBottomSheetDialogFragment
import com.example.grabapp.databinding.BottomSheetFuelBinding
import com.example.grabapp.driver.register.adapter.FuelAdapter
import com.example.grabapp.model.FuelType

class FuelBottomSheet(
    private val onFuelSelected: (String) -> Unit
) : IBottomSheetDialogFragment<BottomSheetFuelBinding>(
    BottomSheetFuelBinding::inflate
) {

    override val TAG: String = "FuelBottomSheet"

    private lateinit var adapter: FuelAdapter

    override fun initViews() {
        setupRecyclerView()
    }

    override fun initObservers() {
    }

    override fun initListeners() {
    }

    private fun setupRecyclerView() {
        val fuelList = FuelType.entries.toList()

        adapter = FuelAdapter(
            items = fuelList,
            onItemClick = { fuel ->
                onFuelSelected(fuel)
                dismiss()
            }
        )

        viewBinding.rvFuel.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FuelBottomSheet.adapter
        }
    }
}

