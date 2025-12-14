package com.example.grabapp.ui.address_selection.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.Insets
import androidx.core.graphics.scale
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.FragmentCheckDirectionBinding
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter
import com.example.grabapp.utils.CurrentOrder
import com.example.grabapp.view.SnackBarCustom
import kotlinx.coroutines.launch
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap

class CheckDirectionFragment :
    BaseFragment<FragmentCheckDirectionBinding, AddressSelectionViewModel>() {

    private var mapLibreMap: MapLibreMap? = null

    override fun getLazyBinding() = lazy {
        FragmentCheckDirectionBinding.inflate(LayoutInflater.from(context))
    }

    override fun getLazyViewModel() = lazy {
        ViewModelProvider(requireActivity())[AddressSelectionViewModel::class.java]
    }

    override fun setUpClick() {
        binding.btnBack.setOnClickListener {
            viewModel.setPage(AddressSelectionPageAdapter.FRAGMENT_DETAIL_ORDER)
        }

        binding.btnNext.setOnClickListener {
            viewModel.createOrder({
                Toast.makeText(requireContext(), "Tạo đơn hàng thành công", Toast.LENGTH_SHORT)
                    .show()
                CurrentOrder.setOrderId(it)
            }) {
                SnackBarCustom(
                    view = binding.root,
                    message = getString(R.string.create_order_fail),
                    backgroundColor = requireContext().getColor(R.color.white),
                    textColor = requireContext().getColor(R.color.green),
                    bottomMarginDp = 100f,
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        observerData()
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.root.setPadding(0)
        (binding.btnBack.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = inset.top
        (binding.btnNext.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin =
            resources.getDimension(R.dimen.size_20).toInt() + inset.bottom
    }

    private fun observerData() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { binding.lottie.isVisible = it }
        }
    }

    private fun initMap() {
        binding.mapView.getMapAsync { map ->
            mapLibreMap = map
            map.setStyle("https://tiles.goong.io/assets/goong_map_web.json?api_key=${AddressRepository.MAP_KEY}") {
                observeDirection(map)
            }
        }
    }

    private fun observeDirection(map: MapLibreMap) {
        val iconFactory = IconFactory.getInstance(requireContext())
        val pickUp = viewModel.orderForm.value.pickupAddress
        val startIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_map)
            .scale(80, 80, false)
            .let { iconFactory.fromBitmap(it) }

        lifecycleScope.launch {
            viewModel.directionResponses.collect { responses ->

                map.clearAll()

                val startLatLng = LatLng(pickUp.latitude, pickUp.longitude)
                map.addMarker(MarkerOptions().position(startLatLng).icon(startIcon))

                val boundsBuilder = LatLngBounds.Builder().include(startLatLng)
                val listRoute = viewModel.getListRoute()
                binding.layoutText.isVisible = listRoute.size != 1
                binding.tvTurn.text = listRoute.joinToString(" -> ") { "Đơn số ${it.packageIndex}" }

                listRoute.forEach { item ->
                    val latLng = LatLng(item.latitude, item.longitude)
                    val numberIcon =
                        iconFactory.fromBitmap(getMarkerBitmap(item.packageIndex.toString()))
                    map.addMarker(MarkerOptions().position(latLng).icon(numberIcon))
                    boundsBuilder.include(latLng)
                }

                if (listRoute.isNotEmpty()) {
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            boundsBuilder.build(),
                            300
                        )
                    )
                }

                responses?.forEach { drawRoute(map, it) }
            }
        }
    }

    private fun MapLibreMap.clearAll() {
        markers.toList().forEach { it.remove() }
        polylines.toList().forEach { removePolyline(it) }
    }

    private fun drawRoute(map: MapLibreMap, response: GoongDirectionApiResponse) {
        val encoded = response.routes?.firstOrNull()?.overview_polyline?.points ?: return
        val decoded = decodePolyline(encoded)

        map.addPolyline(
            PolylineOptions()
                .addAll(decoded)
                .width(6f)
                .color(requireContext().getColor(R.color.main_blue))
        )
    }

    private fun getMarkerBitmap(number: String): android.graphics.Bitmap {
        val view = layoutInflater.inflate(R.layout.layout_marker_number, null)
        view.findViewById<android.widget.TextView>(R.id.tv_index).text = number

        val size = resources.getDimension(R.dimen.size_40).toInt()
        view.measure(
            View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)
        )
        view.layout(0, 0, size, size)

        return android.graphics.Bitmap.createBitmap(
            size,
            size,
            android.graphics.Bitmap.Config.ARGB_8888
        )
            .also { view.draw(android.graphics.Canvas(it)) }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encoded.length) {
            var shift = 0
            var result = 0
            var b: Int

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            lat += if (result and 1 != 0) (result shr 1).inv() else (result shr 1)

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            lng += if (result and 1 != 0) (result shr 1).inv() else (result shr 1)

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }

        return poly
    }
}
