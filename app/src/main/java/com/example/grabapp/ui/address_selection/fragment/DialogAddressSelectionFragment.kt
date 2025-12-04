package com.example.grabapp.ui.address_selection.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.databinding.DialogBottomAddressSelectionBinding
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.respone.Prediction
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.ui.address_selection.adapter.AddressAdapter
import com.example.grabapp.ui.address_selection.adapter.AddressAdapterListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre

class DialogAddressSelectionFragment : BottomSheetDialogFragment() {
    private var _binding: DialogBottomAddressSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var viewModel: AddressSelectionViewModel
    private lateinit var textWatcher: TextWatcher
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapLibre.getInstance(requireContext())
        _binding = DialogBottomAddressSelectionBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            requireActivity()
        )[AddressSelectionViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupFullHeight()
        observerView()
        setupSearchListener()
    }

    private fun initView() {
        addressAdapter = AddressAdapter(mutableListOf(), object : AddressAdapterListener {
            override fun onClickItem(prediction: Prediction) {
                viewModel.setAddress(prediction) {
                    onDestroyView()
                    dismiss()
                }
            }
        })

        binding.rcvAddress.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvAddress.adapter = addressAdapter
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                if (binding.rcvAddress.isVisible) binding.rcvAddress.isVisible = false
                viewModel.showLoading()
            }

            override fun afterTextChanged(p0: Editable?) {
                val text = p0.toString()
                if(text.isEmpty()) return viewModel.setListAddress(emptyList())
                viewModel.searchAddress(text)
            }
        }

        when (viewModel.getLastFocusEdt()) {
            EditTextEnum.DROP_OFF -> {
                binding.edtPickUp.visibility = View.INVISIBLE
                binding.icPickUpLocation.visibility = View.INVISIBLE
                binding.point.isVisible = false
            }

            EditTextEnum.PICK_UP -> {
                binding.edtDropOff.visibility = View.INVISIBLE
                binding.icDropOffLocation.isVisible = false
                binding.point.visibility = View.INVISIBLE
            }

            else -> {
                binding.point.isVisible = true
            }
        }
    }

    private fun observerView() {
        lifecycleScope.launch {
            viewModel.listAddress.collect {
                addressAdapter.updateNewList(it)
                binding.rcvAddress.isVisible = !it.isEmpty()
                binding.empty.isVisible = it.isEmpty()
                viewModel.hideLoading()
            }
        }
        lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.loading.isVisible = it
                if (binding.empty.isVisible && it)
                    binding.empty.isVisible = false
            }
        }
        lifecycleScope.launch {
            viewModel.orderForm.collect {
                binding.edtPickUp.setText(it.pickupAddress.detail)
                binding.edtDropOff.setText(it.listPackageInfo[viewModel.selectPackagePosition].dropOffAddress.detail)
            }
        }
    }

    private fun setupSearchListener() {
        binding.btnBack.setOnClickListener {
            onDestroyView()
            dismiss()
        }
        binding.edtDropOff.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) viewModel.setLastFocusEdt(EditTextEnum.DROP_OFF)
            binding.edtDropOff.text.let {
                if (it.toString().isNotEmpty())
                    viewModel.searchAddress(it.toString())
            }
        }
        binding.edtPickUp.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) viewModel.setLastFocusEdt(EditTextEnum.PICK_UP)
            binding.edtPickUp.text.let {
                if (it.toString().isNotEmpty())
                    viewModel.searchAddress(it.toString())
            }
        }
        binding.edtDropOff.addTextChangedListener(textWatcher)
        binding.edtPickUp.addTextChangedListener(textWatcher)
    }

    private fun setupFullHeight() {
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheet =
                (dialogInterface as BottomSheetDialog)
                    .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.isDraggable = false
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.setBackgroundColor(requireContext().getColor(R.color.white))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setListAddress(emptyList())
    }
}
