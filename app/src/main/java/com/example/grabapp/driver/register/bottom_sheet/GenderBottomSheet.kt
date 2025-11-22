package com.example.grabapp.driver.register.bottom_sheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.IBottomSheetDialogFragment
import com.example.grabapp.databinding.BottomSheetGenderBinding
import com.example.grabapp.driver.register.adapter.GenderAdapter

class GenderBottomSheet(
    private val onGenderSelected: (String) -> Unit
) : IBottomSheetDialogFragment<BottomSheetGenderBinding>(
    BottomSheetGenderBinding::inflate
) {

    override val TAG: String = "GenderBottomSheet"

    private lateinit var adapter: GenderAdapter

    override fun initViews() {
        setupRecyclerView()
    }

    override fun initObservers() {
    }

    override fun initListeners() {
    }

    private fun setupRecyclerView() {
        val genderList = listOf("Nam", "Nữ")

        adapter = GenderAdapter(
            items = genderList,
            onItemClick = { gender ->
                onGenderSelected(gender)
                dismiss()
            }
        )

        viewBinding.rvGender.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@GenderBottomSheet.adapter
        }
    }
}
