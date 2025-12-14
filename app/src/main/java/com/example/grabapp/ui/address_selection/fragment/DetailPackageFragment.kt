package com.example.grabapp.ui.address_selection.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.grabapp.R
import com.example.grabapp.databinding.DialogDetailPackageBinding
import com.example.grabapp.domain.enum.PackageTypeEnum
import com.example.grabapp.domain.enum.SizeEnum
import com.example.grabapp.domain.enum.getSizeEnum
import com.example.grabapp.domain.model.order.PackageItemModel
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.ui.address_selection.adapter.PackageTypeAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.File

class DetailPackageFragment : BottomSheetDialogFragment() {
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Cần cấp quyền camera", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                if (::viewModel.isInitialized && ::photoFile.isInitialized) {
                    viewModel.setImageUri(
                        FileProvider.getUriForFile(
                            requireContext(),
                            "${requireActivity().packageName}.provider",
                            photoFile
                        )
                    ) {
                        binding.iv.setImageURI(viewModel.imageUri.value)
                        setUpBtnCamera(true)
                    }
                }
            }
        }
    private var _binding: DialogDetailPackageBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoFile: File
    private lateinit var viewModel: AddressSelectionViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDetailPackageBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            requireActivity()
        )[AddressSelectionViewModel::class.java]
        photoFile = File.createTempFile(
            "IMG_", ".jpg",
            context?.getExternalFilesDir("Pictures")
        )
        return binding.root
    }

    private fun openCamera() {
        if (::photoFile.isInitialized)
            takePicture.launch(
                FileProvider.getUriForFile(
                    requireContext(),
                    "${requireActivity().packageName}.provider",
                    photoFile
                )
            )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFullHeight()
        observerView()
        setUpView()
    }

    fun setUpView() {
        if (viewModel.imageUri.value != null) {
            binding.iv.setImageURI(viewModel.imageUri.value)
            setUpBtnCamera(true)
        } else setUpBtnCamera(false)
        binding.btnConfirm.alpha = 0.6f
        binding.btnConfirm.isEnabled = false
        binding.rcvType.adapter = PackageTypeAdapter()
        binding.rcvType.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setUpBtnCamera(value: Boolean) {
        binding.layoutAddPhotoSuccess.isVisible = value
        binding.btnDelete.isVisible = value
        binding.tvAddPhoto.isVisible = !value
    }

    fun observerView() {
        binding.tvAddPhoto.setOnClickListener {
            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
        }
        binding.btnConfirm.setOnClickListener {
            onBtnConfirmClick()
        }
        binding.btnDelete.setOnClickListener {
            viewModel.setImageUri(null)
            setUpBtnCamera(false)
        }
        binding.chipGroupSize.setOnCheckedStateChangeListener { group, checkedIds ->
            onChipItemClick(checkedIds, group)
        }
        binding.containerLayout.setOnTouchListener { view, motionEvent ->
            hideKeyboardAndClearFocus()
            false
        }
        binding.edtWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                validateData()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun onChipItemClick(checkedIds: List<Int>, group: ChipGroup) {
        checkedIds.firstOrNull()?.let {
            val chip = group.findViewById<Chip>(it)
            val imgId = when (chip.text) {
                SizeEnum.S.value -> R.drawable.img_s
                SizeEnum.M.value -> R.drawable.img_m
                SizeEnum.L.value -> R.drawable.img_l
                SizeEnum.XL.value -> R.drawable.img_xl
                else -> R.drawable.img_s
            }
            Glide.with(binding.ivDelivery)
                .load(imgId)
                .into(binding.ivDelivery)

            validateData()
        }
    }

    private fun validateData() {
        val selectedChipId = binding.chipGroupSize.checkedChipId
        val maxWeight = when (selectedChipId) {
            R.id.chip_s -> 5
            R.id.chip_m -> 10
            R.id.chip_l -> 15
            R.id.chip_xl -> 20
            else -> 5
        }

        val weightText = binding.edtWeight.text.toString()
        val weight = weightText.toIntOrNull()
        val isValid = weight != null && weight in 1..maxWeight

        binding.tvErrorWeight.isVisible = !isValid
        binding.tvErrorWeight.text = "Tối đa là $maxWeight kg"
        val backgroundRes =
            if (isValid || weightText.isEmpty()) R.drawable.bg_edit_rounded else R.drawable.bg_edt_rounded_red
        binding.edtWeight.setBackgroundResource(backgroundRes)

        if (isValid) {
            binding.btnConfirm.alpha = 1f
            binding.btnConfirm.isEnabled = true
        } else {
            binding.btnConfirm.alpha = 0.6f
            binding.btnConfirm.isEnabled = false
        }
    }

    private fun hideKeyboardAndClearFocus() {
        val currentFocus = dialog?.currentFocus
        if (currentFocus != null) {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            currentFocus.clearFocus()
        }
    }

    private fun onBtnConfirmClick() {
        val kg = binding.edtWeight.text.toString().toIntOrNull() ?: 1
        val selectedText =
            binding.chipGroupSize.findViewById<Chip?>(binding.chipGroupSize.checkedChipId)?.text?.toString()
        val selectedType = binding.rcvType.adapter?.let {
            (it as PackageTypeAdapter).getSelectedType()
        }?: PackageTypeEnum.KHAC
        val newPackageInfo = viewModel.getCurrentPackageInfo().copy(
            weightKg = kg.toDouble(),
            packageSize = getSizeEnum(selectedText ?: ""),
            category = selectedType
        )
        viewModel.updatePackageInfo(newPackageInfo)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}
