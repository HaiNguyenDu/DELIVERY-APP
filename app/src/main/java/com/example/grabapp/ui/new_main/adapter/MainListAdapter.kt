package com.example.grabapp.ui.new_main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.FragmentMainListBinding
import com.example.grabapp.ui.new_main.model.Poster

class MainListAdapter(
    private val listData: List<Poster>
): RecyclerView.Adapter<MainListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MainListAdapter.ViewHolder {
        val binding = FragmentMainListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainListAdapter.ViewHolder, position: Int) {
        holder.onHolder(position)
    }

    override fun getItemCount() = 5

    inner class ViewHolder(val binding: FragmentMainListBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun onHolder(position: Int){
            binding.rcv.layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            binding.rcv.adapter = ListImageAdapter(emptyList())
        }
    }

}