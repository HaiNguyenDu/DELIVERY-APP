package com.example.grabapp.ui.address_selection.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.graphics.scale
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.FragmentCheckDirectionBinding
import com.example.grabapp.databinding.FragmentCheckOrderBinding
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter
import kotlinx.coroutines.launch
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap

class CheckDirectionFragment : BaseFragment<FragmentCheckDirectionBinding, AddressSelectionViewModel>() {

    private var mapLibreMap: MapLibreMap? = null

    override fun getLazyBinding(): Lazy<FragmentCheckDirectionBinding> = lazy {
        FragmentCheckDirectionBinding.inflate(LayoutInflater.from(context))
    }

    override fun getLazyViewModel(): Lazy<AddressSelectionViewModel> = lazy {
        ViewModelProvider(requireActivity())[AddressSelectionViewModel::class.java]
    }

    override fun setUpClick() {
        binding.btnBack.setOnClickListener {
            viewModel.setPage(AddressSelectionPageAdapter.FRAGMENT_DETAIL_ORDER)
        }
        binding.btnNext.setOnClickListener {
            viewModel.setPage(AddressSelectionPageAdapter.FRAGMENT_CHECK_ORDER)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMap()
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.root.setPadding(0)
        binding.btnBack.layoutParams.let {
            if (it is ViewGroup.MarginLayoutParams)
                it.topMargin = inset.top
        }
        binding.btnNext.layoutParams.let {
            if (it is ViewGroup.MarginLayoutParams)
                it.bottomMargin =
                    requireContext().resources.getDimension(R.dimen.size_20).toInt() + inset.bottom
        }
    }

    private fun setUpMap() {
        viewModel.getDirection()
        binding.mapView.getMapAsync { map ->
            mapLibreMap = map
            map.setStyle(
                "https://tiles.goong.io/assets/goong_map_web.json?api_key=${AddressRepository.MAP_KEY}"
            ) {
                val iconFactory = IconFactory.getInstance(requireContext())

                val pickUp = viewModel.orderForm.value.pickupAddress
                val dropOff = viewModel.getCurrentPackageInfo().dropOffAddress

                val startBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_map)
                    .scale(80, 80, false)
                val startIcon = iconFactory.fromBitmap(startBitmap)

                val startLatLng = LatLng(pickUp.latitude, pickUp.longitude)
                val endLatLng = LatLng(dropOff.latitude, dropOff.longitude)

                map.addMarker(
                    MarkerOptions().position(startLatLng).icon(startIcon).title("Điểm đi")
                )
                map.addMarker(MarkerOptions().position(endLatLng).icon(startIcon).title("Điểm đến"))

                val bounds = LatLngBounds.Builder()
                    .include(startLatLng)
                    .include(endLatLng)
                    .build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300))

                lifecycleScope.launch {
                    viewModel.directionResponse.collect { response ->
                        if (response != null) {
                            drawRoute(map, response)
                        }
                    }
                }
            }
        }
    }

    private fun drawRoute(map: MapLibreMap, response: GoongDirectionApiResponse) {
        val route = response.routes?.firstOrNull() ?: return
        val encodedPolyline = route.overview_polyline?.points ?: return

        val decodedPath = decodePolyline(encodedPolyline)

        val lineOptions = PolylineOptions()
            .addAll(decodedPath)
            .width(6f)
            .color(requireActivity().getColor(R.color.main_blue))

        map.addPolyline(lineOptions)
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            val latLng = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(latLng)
        }

        return poly
    }
}
