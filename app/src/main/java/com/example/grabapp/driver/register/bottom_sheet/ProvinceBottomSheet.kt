package com.example.grabapp.driver.register.bottom_sheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.IBottomSheetDialogFragment
import com.example.grabapp.databinding.BottomSheetProvinceBinding
import com.example.grabapp.driver.register.adapter.ProvinceAdapter
import com.example.grabapp.driver.register.data.VietnamProvinces

class ProvinceBottomSheet(
    private val onProvinceSelected: (String) -> Unit
) : IBottomSheetDialogFragment<BottomSheetProvinceBinding>(
    BottomSheetProvinceBinding::inflate
) {

    override val TAG: String = "ProvinceBottomSheet"

    private lateinit var adapter: ProvinceAdapter

    override fun initViews() {
        setupRecyclerView()
    }

    override fun initObservers() {
    }

    override fun initListeners() {
    }

    private fun setupRecyclerView() {
        val provinceList = VietnamProvinces.provinces

        adapter = ProvinceAdapter(
            items = provinceList,
            onItemClick = { province ->
                onProvinceSelected(province)
                dismiss()
            }
        )

        viewBinding.rvProvince.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ProvinceBottomSheet.adapter
        }
    }
}

