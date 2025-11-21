package com.example.grabapp.driver.order_detail

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.graphics.scale
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.ActivityDetailOrderBinding
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.model.Address
import com.example.grabapp.model.Order
import android.os.Parcelable
import com.example.grabapp.model.OrderState
import com.example.grabapp.respone.Coordinates
import kotlinx.coroutines.launch
import com.example.grabapp.respone.GoongDirectionApiResponse
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap

class OrderDetailActivity : BaseActivity<ActivityDetailOrderBinding, OrderDetailViewModel>() {

    companion object {
        private const val EXTRA_ORDER = "extra_order"
    }

    private lateinit var order: Order
    private var currentState: OrderState = OrderState.RECEIVED_ORDER

    private var positioning: Address? = null

    private val pickUpAddress: Address = Address(
        coordinates = Coordinates(16.07367333700006, 108.14992938100005),
        name = "DUT",
        address = "Đại học Bách khoa Đà Nẵng, 54 Nguyễn Lương Bằng, Hòa Khánh Bắc, Liên Chiểu, Đà Nẵng"
    )

    private val dropOffAddress: Address = Address(
        coordinates = Coordinates(16.07079150000004, 108.14888825800006),
        name = "Chợ Hòa Khánh",
        address = "Chợ Hòa Khánh, Âu Cơ, Hòa Khánh Bắc, Liên Chiểu, Đà Nẵng"
    )

    private var mapLibreMap: MapLibreMap? = null
    private lateinit var repository: AddressRepository
    private var directionResponse: GoongDirectionApiResponse? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationPermission =
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationPermission && coarseLocationPermission) {
            getCurrentLocation()
        }
    }

    override fun getLazyBinding(): Lazy<ActivityDetailOrderBinding> =
        lazy { ActivityDetailOrderBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<OrderDetailViewModel> =
        lazy { OrderDetailViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = AddressRepository.getInstance(this)
        setupClickListeners()
        getOrderFromIntent()
        setupToolbar()
        setupOrderInformation()
        setupStateManagement()
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        lifecycleScope.launch {
            try {
                repository.getCurrentLocation()?.let { location ->
                    positioning = Address(
                        coordinates = Coordinates(location.latitude, location.longitude),
                        name = "Vị trí hiện tại",
                        address = "Vị trí hiện tại của tài xế"
                    )
                    getDirection()
                }
            } catch (e: Exception) {
            } finally {
                setUpMap()
            }
        }
    }

    private fun getDirection() {
        positioning?.let { startAddress ->
            repository.getDirectionData(
                dropOff = pickUpAddress,
                pickUp = startAddress,
                onSuccess = { response ->
                    directionResponse = response
                    mapLibreMap?.let { map ->
                        drawRoute(map, response)
                    }
                },
                onError = { error ->
                }
            )
        }
    }

    private fun setupClickListeners() {
        binding.tvDeliveryComplete.onClickWithScale {
            moveToNextState()
        }
    }

    private fun getOrderFromIntent() {
        order = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_ORDER, Order::class.java) ?: Order.getMockOrder()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Parcelable>(EXTRA_ORDER) as? Order ?: Order.getMockOrder()
        }
    }

    private fun setupToolbar() {
        binding.apply {
            ivBack.onClickWithScale {
                onBackPressedDispatcher.onBackPressed()
            }
            tvOrderId.text = order.orderId
        }
    }

    private fun setupOrderInformation() {
        binding.apply {
            tvPickUpName.text = order.pickerName
            tvPickUpAddress.text = order.pickerAddress
            tvCallInAdvance.visibility = if (order.notion != null) View.VISIBLE else View.GONE
            tvCallInAdvance.text = order.notion

            tvReceiverName.text = order.deliveryName
            tvReceiverAddress.text = order.deliveryAddress
            tvCallReceiver.visibility = if (order.isBusinessHours) View.VISIBLE else View.GONE

            tvGoodsDes.text = order.orderType.displayName
            tvGoodsWeight.text = order.goodsWeight
            tvDistance.text = order.distance
            tvDeliveryFee.text = "${String.format("%,d", order.income)}đ"
        }
    }

    private fun setupStateManagement() {
        currentState = OrderState.RECEIVED_ORDER
        updateUIForCurrentState()
    }

    private fun moveToNextState() {
        currentState = when (currentState) {
            OrderState.RECEIVED_ORDER -> OrderState.COMING_TO_PICKUP
            OrderState.COMING_TO_PICKUP -> OrderState.RECEIVED_GOODS
            OrderState.RECEIVED_GOODS -> OrderState.DELIVERING
            OrderState.DELIVERING -> OrderState.DELIVERED
            OrderState.DELIVERED -> return
            OrderState.CANCELED -> return
        }
        updateUIForCurrentState()
    }

    private fun updateUIForCurrentState() {
        val paddingPx = resources.getDimensionPixelSize(R.dimen.size_8)

        when (currentState) {
            OrderState.RECEIVED_ORDER -> {
                binding.apply {
                    ivReceivedOrder.setImageResource(R.drawable.ic_white_tick)
                    ivReceivedOrder.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivReceivedOrderStatus.visibility = View.VISIBLE
                }
                binding.tvDeliveryComplete.text = getString(R.string.ang_n_l_y_h_ng)
            }

            OrderState.COMING_TO_PICKUP -> {
                binding.apply {
                    ivComingTo.setImageResource(R.drawable.ic_white_tick)
                    ivComingTo.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivComingTo.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    tvComingTo.setTextColor(getColor(R.color.black))
                    ivComingToStatus.visibility = View.VISIBLE

                    tvDeliveryComplete.text = getString(R.string.received_goods)
                }
            }

            OrderState.RECEIVED_GOODS -> {
                binding.apply {
                    ivReceivedGood.setImageResource(R.drawable.ic_white_tick)
                    ivReceivedGood.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivReceivedGood.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    tvReceivedGood.setTextColor(getColor(R.color.black))
                    ivReceivedGoodStatus.visibility = View.VISIBLE

                    tvDeliveryComplete.text = getString(R.string.ang_giao_h_ng)
                }
            }

            OrderState.DELIVERING -> {
                binding.apply {
                    ivDelivering.setImageResource(R.drawable.ic_white_tick)
                    ivDelivering.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivDelivering.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    tvDelivering.setTextColor(getColor(R.color.black))
                    ivDeliveringStatus.visibility = View.VISIBLE

                    tvDeliveryComplete.text = getString(R.string.ho_n_th_nh_giao_h_ng)
                }
            }

            OrderState.DELIVERED -> {
                binding.apply {
                    ivDelivered.setImageResource(R.drawable.ic_white_tick)
                    ivDelivered.setBackgroundResource(R.drawable.bg_gradient_finding_orders_100)
                    ivDelivered.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                    tvDelivered.setTextColor(getColor(R.color.black))
                    ivDeliveredStatus.visibility = View.VISIBLE

                    tvDeliveryComplete.text = getString(R.string.chi_ti_t_n_h_ng)
                }
            }

            OrderState.CANCELED -> {
            }
        }
    }

    override fun handleInsets(v: View, insets: Insets) {
        binding.toolbar.setPadding(0, insets.top, 0, 0)
    }

    private fun setUpMap() {
        binding.mapView.getMapAsync { map ->
            mapLibreMap = map
            map.setStyle(
                "https://tiles.goong.io/assets/goong_map_web.json?api_key=${AddressRepository.MAP_KEY}"
            ) {
                val iconFactory = IconFactory.getInstance(this)

                val driverBitmap =
                    BitmapFactory.decodeResource(resources, R.drawable.ic_driver_delivering)
                        .scale(80, 80, false)
                val driverIcon = iconFactory.fromBitmap(driverBitmap)

                val endBitmap =
                    BitmapFactory.decodeResource(resources, R.drawable.ic_map)
                        .scale(80, 80, false)
                val endIcon = iconFactory.fromBitmap(endBitmap)

                val startLatLng = positioning?.let {
                    LatLng(it.coordinates.lat, it.coordinates.lng)
                }
                val endLatLng = LatLng(pickUpAddress.coordinates.lat, pickUpAddress.coordinates.lng)

                if (startLatLng != null) {
                    map.addMarker(
                        MarkerOptions().position(startLatLng).icon(driverIcon)
                            .title("Vị trí hiện tại")
                    )
                }
                map.addMarker(
                    MarkerOptions().position(endLatLng).icon(endIcon).title("Điểm lấy hàng")
                )

                if (startLatLng != null) {
                    val bounds = LatLngBounds.Builder()
                        .include(startLatLng)
                        .include(endLatLng)
                        .build()
                    binding.mapView.post {
                        try {
                            val padding = 150

                            val cameraUpdate =
                                CameraUpdateFactory.newLatLngBounds(bounds, padding)
                            map.animateCamera(cameraUpdate)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 12.0))
                        }
                    }

                } else {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(endLatLng, 15.0))
                }

                directionResponse?.let { response ->
                    drawRoute(map, response)
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
            .color(getColor(R.color.main_blue))

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

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}
