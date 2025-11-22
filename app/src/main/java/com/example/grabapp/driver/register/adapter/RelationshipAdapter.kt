package com.example.grabapp.driver.register.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemRelationshipBinding
import com.example.grabapp.model.Relationship

class RelationshipAdapter(
    private val items: List<Relationship>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<RelationshipAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRelationshipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(
        private val binding: ItemRelationshipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(relationship: Relationship) {
            binding.tvRelationship.text = relationship.relationship
            binding.root.setOnClickListener {
                onItemClick(relationship.relationship)
            }
        }
    }
}

