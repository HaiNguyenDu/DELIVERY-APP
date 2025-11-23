package com.example.grabapp.ui.address_selection.fragment

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scale
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.grabapp.R
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.FragmentDropOffInfoBinding
import com.example.grabapp.domain.enum.EditTextEnum
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap

class DialogLocationInfoFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDropOffInfoBinding? = null
    private val binding get() = _binding!!

    private var mapLibreMap: MapLibreMap? = null
    private lateinit var viewModel: AddressSelectionViewModel
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
        setupMap()
        setupValidateRealtime()
    }

    private fun setupUI() = with(binding) {
        title.text = when (viewModel.getLastEdtTextClicked()) {
            EditTextEnum.DROP_OFF -> "Thông tin điểm giao hàng"
            EditTextEnum.PICK_UP -> "Thông tin điểm nhận hàng"
            else -> ""
        }

        edtAddress.setText(
            when (viewModel.getLastEdtTextClicked()) {
                EditTextEnum.DROP_OFF -> viewModel.dropOffAddress.value.getFormattedAddress()
                EditTextEnum.PICK_UP -> viewModel.pickUpAddress.value.getFormattedAddress()
                else -> ""
            }
        )

        edtAddress.setOnClickListener {
            DialogAddressSelectionFragment().show(
                parentFragmentManager, "ChangeAddress"
            )
        }

        btnBack.setOnClickListener { dismiss() }
    }

    private fun setupMap() {
        val address = when (viewModel.getLastEdtTextClicked()) {
            EditTextEnum.DROP_OFF -> viewModel.dropOffAddress.value
            else -> viewModel.pickUpAddress.value
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

                val latLng = LatLng(address.coordinates.lat, address.coordinates.lng)

                map.addMarker(MarkerOptions().position(latLng).icon(icon))
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
                dialogInterface as? com.google.android.material.bottomsheet.BottomSheetDialog
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

                it.setOnApplyWindowInsetsListener { _, insets ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                        val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
                        if (isImeVisible)
                            hideMapSmooth()
                        else
                            showMapSmooth()
                        binding.scrollView.setPadding(
                            0,
                            0,
                            0,
                            systemInsets.bottom + imeInsets.bottom
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
    override fun onResume() = super.onResume().also { binding.mapView.onResume() }
    override fun onPause() = super.onPause().also { binding.mapView.onPause() }
    override fun onStop() = super.onStop().also { binding.mapView.onStop() }
    override fun onLowMemory() = super.onLowMemory().also { binding.mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) =
        super.onSaveInstanceState(outState).also { binding.mapView.onSaveInstanceState(outState) }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        viewModel.apply {
            setLastFocusEdt(EditTextEnum.NOT_THING)
            setListAddress(emptyList())
        }
        _binding = null
    }
}
