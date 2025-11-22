package com.example.grabapp.driver.register.bottom_sheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.IBottomSheetDialogFragment
import com.example.grabapp.databinding.BottomSheetDrivingLicenseBinding
import com.example.grabapp.driver.register.adapter.DrivingLicenseAdapter
import com.example.grabapp.model.DrivingLicense

class DrivingLicenseBottomSheet(
    private val onCategorySelected: (String) -> Unit
) : IBottomSheetDialogFragment<BottomSheetDrivingLicenseBinding>(
    BottomSheetDrivingLicenseBinding::inflate
) {

    override val TAG: String = "DrivingLicenseBottomSheet"

    private lateinit var adapter: DrivingLicenseAdapter

    override fun initViews() {
        setupRecyclerView()
    }

    override fun initObservers() {
    }

    override fun initListeners() {
    }

    private fun setupRecyclerView() {
        val categoryList = DrivingLicense.entries.map { it.name }

        adapter = DrivingLicenseAdapter(
            items = categoryList,
            onItemClick = { category ->
                onCategorySelected(category)
                dismiss()
            }
        )

        viewBinding.rvDrivingLicense.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DrivingLicenseBottomSheet.adapter
        }
    }
}

