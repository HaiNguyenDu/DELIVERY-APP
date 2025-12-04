package com.example.grabapp.ui.address_selection.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.grabapp.databinding.FragmentCheckOrderBinding
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.ui.address_selection.adapter.AddressSelectionPageAdapter
import com.example.grabapp.view.SnackBarCustom
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
            viewModel.createOrder({
                SnackBarCustom(
                    view = binding.root,
                    message = getString(R.string.create_order_success),
                    backgroundColor = context?.getColor(R.color.white)!!,
                    textColor = context?.getColor(R.color.green)!!,
                    bottomMarginDp = 100f,
                ).show()
            }){
                SnackBarCustom(
                    view = binding.root,
                    message = getString(R.string.create_order_fail),
                    backgroundColor = context?.getColor(R.color.white)!!,
                    textColor = context?.getColor(R.color.green)!!,
                    bottomMarginDp = 100f,
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMap()
        observerData()
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

    private fun observerData(){
        lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.lottie.isVisible = it
            }
        }
    }
    private fun setUpMap() {
        binding.mapView.getMapAsync { map ->
            mapLibreMap = map

            map.setStyle(
                "https://tiles.goong.io/assets/goong_map_web.json?api_key=${AddressRepository.MAP_KEY}"
            ) {
                val iconFactory = IconFactory.getInstance(requireContext())
                val pickUp = viewModel.orderForm.value.pickupAddress

                val startBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_map)
                    .scale(80, 80, false)
                val startIcon = iconFactory.fromBitmap(startBitmap)

                lifecycleScope.launch {
                    viewModel.directionResponses.collect { responses ->
                        map.markers?.let {
                            map.markers.forEach {
                                it.remove()
                            }
                            map.polylines.forEach { map.removePolyline(it) }

                        }


                        val startLatLng = LatLng(pickUp.latitude, pickUp.longitude)
                        map.addMarker(
                            MarkerOptions().position(startLatLng).icon(startIcon).title("Điểm đi")
                        )
                        val listRoute = viewModel.getListRoute()
                        val boundsBuilder = LatLngBounds.Builder()
                        boundsBuilder.include(startLatLng)
                        listRoute.forEach { item ->
                            val latLng = LatLng(item.latitude, item.longitude)
                            map.addMarker(
                                MarkerOptions().position(latLng).icon(startIcon).title("Điểm đến")
                            )
                            boundsBuilder.include(latLng)
                        }
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 300)
                        )
                        Log.d("DEBUG_ROUTE", "Response route count = ${responses?.size}")
                        responses?.forEach { routeResponse ->
                            drawRoute(map, routeResponse)
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
