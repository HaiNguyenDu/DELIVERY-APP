package com.example.grabapp.ui.address_selection.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentMainOrderBinding
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.ui.address_selection.adapter.HistoryNearAdapter
import kotlinx.coroutines.launch

class MainOrderFragment : BaseFragment<FragmentMainOrderBinding, AddressSelectionViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentMainOrderBinding> = lazy {
        FragmentMainOrderBinding.inflate(LayoutInflater.from(context))
    }

    override fun getLazyViewModel(): Lazy<AddressSelectionViewModel> = lazy {
        ViewModelProvider(requireActivity())[AddressSelectionViewModel::class.java]
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(inset.left, 0, inset.right, bottomInset)
        binding.layoutHeader.apply {
            setPadding(paddingLeft, inset.top, paddingLeft, paddingBottom)
        }
    }

    override fun setUpClick() {
        binding.tvDropOffLocation.setOnClickListener {
            viewModel.setLastEdtTextClicked(EditTextEnum.NOT_THING)
            DialogAddressSelectionFragment().show(requireActivity().supportFragmentManager,"AddressSelection")
        }
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerData()
        initView()
    }

    private fun initView(){
        binding.rcvHistory.adapter = HistoryNearAdapter()
        binding.rcvHistory.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)
    }
    private fun observerData(){
        lifecycleScope.launch {
            viewModel.pickUpAddress.collect {
                binding.tvPickUpLocation.text = it.getFormattedAddress()
            }
        }
        lifecycleScope.launch {
            viewModel.dropOffAddress.collect {
                val text = it.getFormattedAddress()
                if(text.isEmpty())
                {
                    binding.tvDropOffLocation.text = getString(R.string.giao_den_dau)
                    binding.tvDropOffLocation.setTextColor(resources.getColor(R.color.grey))
                }
                else{
                    binding.tvDropOffLocation.text = text
                    binding.tvDropOffLocation.setTextColor(resources.getColor(R.color.black))
                }
            }
        }
    }

}