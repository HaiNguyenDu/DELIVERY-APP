package com.example.grabapp.driver.register.bottom_sheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.IBottomSheetDialogFragment
import com.example.grabapp.databinding.BottomSheetVehicleBrandBinding
import com.example.grabapp.driver.register.adapter.VehicleBrandAdapter
import com.example.grabapp.model.VehicleBrand

class VehicleBrandBottomSheet(
    private val onBrandSelected: (String) -> Unit
) : IBottomSheetDialogFragment<BottomSheetVehicleBrandBinding>(
    BottomSheetVehicleBrandBinding::inflate
) {

    override val TAG: String = "VehicleBrandBottomSheet"

    private lateinit var adapter: VehicleBrandAdapter

    override fun initViews() {
        setupRecyclerView()
    }

    override fun initObservers() {
    }

    override fun initListeners() {
    }

    private fun setupRecyclerView() {
        val brandList = VehicleBrand.entries.toList()

        adapter = VehicleBrandAdapter(
            items = brandList,
            onItemClick = { brand ->
                onBrandSelected(brand)
                dismiss()
            }
        )

        viewBinding.rvVehicleBrand.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@VehicleBrandBottomSheet.adapter
        }
    }
}

