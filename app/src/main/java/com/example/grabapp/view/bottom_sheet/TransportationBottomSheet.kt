package com.example.grabapp.view.bottom_sheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.IBottomSheetDialogFragment
import com.example.grabapp.databinding.BottomSheetTransportationBinding
import com.example.grabapp.driver.transportation.adapter.TransportationAdapter
import com.example.grabapp.model.Transportation

class TransportationBottomSheet(
    private val selectedTransportation: Transportation,
    private val onTransportationSelected: (Transportation) -> Unit
) : IBottomSheetDialogFragment<BottomSheetTransportationBinding>(
    BottomSheetTransportationBinding::inflate
) {

    override val TAG: String = "TransportationBottomSheet"

    private lateinit var adapter: TransportationAdapter

    override fun initViews() {
        setupRecyclerView()
    }

    override fun initObservers() {
    }

    override fun initListeners() {
    }

    private fun setupRecyclerView() {
        val transportationList = Transportation.entries.toList()

        if (transportationList.isEmpty()) {
            return
        }

        adapter = TransportationAdapter(
            items = transportationList,
            selectedTransportation = selectedTransportation,
            onItemClick = { transportation ->
                onTransportationSelected(transportation)
                dismiss()
            }
        )

        viewBinding.rvTransportation.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TransportationBottomSheet.adapter
        }
    }
}
