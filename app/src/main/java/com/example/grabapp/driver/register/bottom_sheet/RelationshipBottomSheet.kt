package com.example.grabapp.driver.register.bottom_sheet

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.base.IBottomSheetDialogFragment
import com.example.grabapp.databinding.BottomSheetRelationshipBinding
import com.example.grabapp.driver.register.adapter.RelationshipAdapter
import com.example.grabapp.model.Relationship

class RelationshipBottomSheet(
    private val onRelationshipSelected: (String) -> Unit
) : IBottomSheetDialogFragment<BottomSheetRelationshipBinding>(
    BottomSheetRelationshipBinding::inflate
) {

    override val TAG: String = "RelationshipBottomSheet"

    private lateinit var adapter: RelationshipAdapter

    override fun initViews() {
        setupRecyclerView()
    }

    override fun initObservers() {
    }

    override fun initListeners() {
    }

    private fun setupRecyclerView() {
        val relationshipList = Relationship.entries.toList()

        adapter = RelationshipAdapter(
            items = relationshipList,
            onItemClick = { relationship ->
                onRelationshipSelected(relationship)
                dismiss()
            }
        )

        viewBinding.rvRelationship.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RelationshipBottomSheet.adapter
        }
    }
}

