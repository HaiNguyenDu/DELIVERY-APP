package com.example.grabapp.ui.address_selection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grabapp.databinding.ItemAddressBinding
import com.example.grabapp.respone.Prediction

class AddressAdapter(
    private val listPredictions: MutableList<Prediction> = mutableListOf<Prediction>(),
    private val addressAdapterListener: AddressAdapterListener? = null
) : RecyclerView.Adapter<AddressAdapter.AddressAdapterViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressAdapterViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AddressAdapterViewHolder,
        position: Int
    ) {
        val prediction = listPredictions[position]
        holder.onHolder(prediction)
    }

    override fun getItemCount() = listPredictions.size

    fun updateNewList(newList: List<Prediction>) {
        this.listPredictions.clear()
        listPredictions.addAll(newList)
        notifyDataSetChanged()
    }

    inner class AddressAdapterViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onHolder(prediction: Prediction) {
            binding.tvMain.text = prediction.structured_formatting?.main_text
            binding.tvSecondary.text = prediction.description
            binding.root.setOnClickListener {
                addressAdapterListener?.onClickItem(prediction)
            }
        }
    }
}
interface AddressAdapterListener{
    fun onClickItem(prediction: Prediction)
}
