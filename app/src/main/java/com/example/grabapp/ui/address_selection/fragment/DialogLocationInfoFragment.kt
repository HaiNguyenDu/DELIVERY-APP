package com.example.grabapp.ui.address_selection.fragment

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scale
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.FragmentDropOffInfoBinding
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.view.SnackBarCustom
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

class DialogLocationInfoFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentDropOffInfoBinding? = null
    private val binding get() = _binding!!

    private var mapLibreMap: MapLibreMap? = null
    private lateinit var viewModel: AddressSelectionViewModel
    private var currentMarker: Marker? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDropOffInfoBinding.inflate(inflater, container, false)
        MapLibre.getInstance(requireContext())
        viewModel = ViewModelProvider(requireActivity())[AddressSelectionViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupFullHeight()
        setupValidateRealtime()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.orderForm.collect {
                val currentPackage = viewModel.getCurrentPackageInfo()
                val text = if (viewModel.getLastFocusEdt() == EditTextEnum.DROP_OFF)
                    currentPackage.dropOffAddress.detail
                else if (viewModel.getLastFocusEdt() == EditTextEnum.PICK_UP) {
                    it.pickupAddress.detail
                } else ""
                binding.edtAddress.setText(text)
                binding.edtName.setText(currentPackage.dropOffAddress.name)
                binding.edtPhone.setText(currentPackage.dropOffAddress.phone)
                binding.edtNote.setText(currentPackage.description)
                setupMap()
            }
        }
    }

    private fun setupUI() = with(binding) {
        title.text = when (viewModel.getLastFocusEdt()) {
            EditTextEnum.DROP_OFF -> "Thông tin điểm giao hàng"
            EditTextEnum.PICK_UP -> "Thông tin điểm nhận hàng"
            else -> ""
        }

        edtAddress.isFocusable = false
        edtAddress.isFocusableInTouchMode = false
        edtAddress.setOnClickListener {
            DialogAddressSelectionFragment().show(
                parentFragmentManager, "ChangeAddress"
            )
        }

        btnBack.setOnClickListener {
            viewModel.onBackPressLocationInfo()
            dismiss()
        }

        btnConfirm.setOnClickListener {
            val currentPackage = viewModel.getCurrentPackageInfo()
            if (isHashInfo()) {

                val newPackage = currentPackage.copy(
                    dropOffAddress = currentPackage.dropOffAddress.copy(
                        name = edtName.text.toString(),
                        phone = edtPhone.text.toString(),
                        detail = edtDetailAddress.text.toString()+"," + currentPackage.dropOffAddress.detail,
                        note = edtNote.text.toString()
                    )
                )
                viewModel.updatePackageInfo(newPackage)
                Log.d("test", newPackage.dropOffAddress.detail)
                dismiss()
            } else {
                SnackBarCustom(
                    view = binding.root,
                    message = getString(R.string.fill_all_edt),
                    backgroundColor = context?.getColor(R.color.white)!!,
                    textColor = context?.getColor(R.color.green)!!,
                    bottomMarginDp = 100f,
                ).show()
            }
        }
    }

    fun isHashInfo(): Boolean {
        var data = false
        binding.apply {
            data = edtPhone.text.isNotEmpty() &&
                    edtName.text.isNotEmpty()
        }
        return data
    }

    private fun setupMap() {
        val address = when (viewModel.getLastFocusEdt()) {
            EditTextEnum.DROP_OFF -> viewModel.getCurrentPackageInfo().dropOffAddress
            else -> viewModel.orderForm.value.pickupAddress
        }

        binding.mapView.getMapAsync { map ->
            mapLibreMap = map
            map.setStyle(
                "https://tiles.goong.io/assets/goong_map_web.json?api_key=${AddressRepository.MAP_KEY}"
            ) {
                val iconFactory = IconFactory.getInstance(requireContext())
                val resizedBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_map)
                    .scale(100, 100, false)
                val icon = iconFactory.fromBitmap(resizedBitmap)
                val latLng = LatLng(address.latitude, address.longitude)
                currentMarker?.remove()
                currentMarker = map.addMarker(MarkerOptions().position(latLng).icon(icon))
                map.cameraPosition = CameraPosition.Builder()
                    .target(latLng)
                    .zoom(15.0)
                    .build()
            }
        }
    }

    private fun setupFullHeight() {
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog =
                dialogInterface as? BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                    isDraggable = false
                }

                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.setBackgroundColor(requireContext().getColor(R.color.white))
                it.requestLayout()
                it.setOnApplyWindowInsetsListener { _, insets ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                        val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
                        if (isImeVisible)
                            hideMapSmooth()
                        else
                            showMapSmooth()
                        val bottom = maxOf(systemInsets.bottom, imeInsets.bottom)
                        binding.layoutInfo.setPadding(
                            0,
                            0,
                            0,
                            bottom + resources.getDimensionPixelSize(R.dimen.size_20)
                        )
                    }
                    insets
                }
            }
        }
    }

    private fun hideMapSmooth() {
        binding.mapView.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(220)
            .withEndAction {
                binding.mapView.visibility = View.GONE
            }
            .start()
    }

    private fun showMapSmooth() {
        binding.mapView.visibility = View.VISIBLE
        binding.mapView.alpha = 0f
        binding.mapView.scaleX = 0.8f
        binding.mapView.scaleY = 0.8f

        binding.mapView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(220)
            .start()
    }


    private fun setupValidateRealtime() = with(binding) {

        edtDetailAddress.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && edtDetailAddress.text.isEmpty()) {
                edtDetailAddress.error = "Vui lòng nhập số tầng / số nhà"
            }
        }

        edtName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && edtName.text.isEmpty()) {
                edtName.error = "Vui lòng nhập tên"
            }
        }

        edtPhone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val phone = edtPhone.text.toString().trim()
                if (phone.isEmpty()) {
                    edtPhone.error = "Vui lòng nhập số điện thoại"
                } else if (!phone.matches(Regex("^(0|\\+84)[0-9]{9,10}$"))) {
                    edtPhone.error = "Số điện thoại không hợp lệ"
                }
            }
        }

        edtNote.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && edtNote.text.isEmpty()) {
                edtNote.error = "Vui lòng nhập ghi chú"
            }
        }
    }

    override fun onStart() = super.onStart().also { binding.mapView.onStart() }
    override fun onResume() = super.onResume().also {
        binding.mapView.onResume()
    }

    override fun onPause() = super.onPause().also { binding.mapView.onPause() }
    override fun onStop() = super.onStop().also { binding.mapView.onStop() }
    override fun onLowMemory() = super.onLowMemory().also { binding.mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) =
        super.onSaveInstanceState(outState).also { binding.mapView.onSaveInstanceState(outState) }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        viewModel.apply {
            setListAddress(emptyList())
        }
        _binding = null
    }
}
