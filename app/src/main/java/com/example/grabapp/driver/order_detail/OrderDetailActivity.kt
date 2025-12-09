package com.example.grabapp.driver.order_detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.graphics.scale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.databinding.ActivityDetailOrderBinding
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.model.Address
import com.example.grabapp.model.DeliveryAddressItem
import com.example.grabapp.model.Order
import com.example.grabapp.model.OrderState
import com.example.grabapp.model.OrderStatus
import com.example.grabapp.respone.GoongDirectionApiResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.Polyline
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap

class OrderDetailActivity : BaseActivity<ActivityDetailOrderBinding, OrderDetailViewModel>() {

    companion object {
        private const val EXTRA_ORDER = "extra_order"
    }

    private var mapLibreMap: MapLibreMap? = null

    private var startMarker: Marker? = null
    private var endMarker: Marker? = null
    private var routePolyline: Polyline? = null
    private var autoUpdateJob: kotlinx.coroutines.Job? = null

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
        lazy {
            val orderRepository = OrderRepository(this)
            val addressRepository = AddressRepository.getInstance(this)
            OrderDetailViewModel(application, orderRepository, addressRepository)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupClickListeners()
        getOrderFromIntent()
        setupToolbar()
        observeViewModel()
        requestLocationPermission()
        setupAutoUpdate()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.order.collect { order ->
                        order?.let {
                            setupOrderInformation(it)
                        }
                    }
                }

                launch {
                    viewModel.addressItems.collect { items ->
                        if (items.isNotEmpty()) {
                            setupRecyclerView(items)
                        }
                    }
                }
                
                launch {
                    viewModel.orderStatus.collect { status ->
                        // Update adapter when status changes
                        val items = viewModel.addressItems.value
                        if (items.isNotEmpty()) {
                            setupRecyclerView(items)
                        }
                    }
                }

                launch {
                    viewModel.currentState.collect { state ->
                        updateMapForState()
                    }
                }

                launch {
                    viewModel.directionResponse.collect { response ->
                        response?.let {
                            mapLibreMap?.let { map ->
                                drawRoute(map, it)
                            }
                        }
                    }
                }
                
                launch {
                    viewModel.orderStatus.collect { status ->
                        status?.let {
                            updateOrderStatusDisplay(it)
                        }
                    }
                }
                
                launch {
                    viewModel.isDeliveryCompleteEnabled.collect { enabled ->
                        binding.tvDeliveryComplete.isEnabled = enabled
                        binding.tvDeliveryComplete.alpha = if (enabled) 1.0f else 0.5f
                    }
                }
            }
        }
    }
    
    private fun setupAutoUpdate() {
        autoUpdateJob = lifecycleScope.launch {
            delay(10000) // 10 seconds
            val orderStatus = viewModel.orderStatus.value
            if (orderStatus == OrderStatus.DRIVER_ASSIGNED) {
                // Auto update to DRIVER_EN_ROUTE_PICKUP if user hasn't clicked
                viewModel.updateOrderStatus(
                    OrderStatus.DRIVER_EN_ROUTE_PICKUP,
                    onSuccess = {
                        val addressItems = viewModel.addressItems.value
                        if (addressItems.isNotEmpty()) {
                            setupRecyclerView(addressItems)
                        }
                    },
                    onError = {}
                )
            }
        }
    }
    
    private fun updateOrderStatusDisplay(status: OrderStatus) {
        binding.tvStatus.text = status.statusName
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
        viewModel.getCurrentLocation(
            onSuccess = { address ->
                getDirection()
            },
            onError = { exception ->
                // Handle error
            }
        )
        setUpMap()
    }

    private fun getDirection() {
        val pickUpAddress = viewModel.getPickUpAddress()
        getDirectionToAddress(pickUpAddress)
    }

    private fun getDirectionToDropOff() {
        val dropOffAddress = viewModel.getDropOffAddress()
        getDirectionToAddress(dropOffAddress)
    }

    private fun getDirectionToAddress(destination: Address) {
        viewModel.getDirectionToAddress(
            destination = destination,
            onSuccess = { response ->
                mapLibreMap?.let { map ->
                    drawRoute(map, response)
                }
            },
            onError = { error ->
                // Handle error
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupClickListeners() {
        binding.tvDeliveryComplete.onClickWithScale {
            if (binding.tvDeliveryComplete.isEnabled) {
                viewModel.updateOrderStatus(
                    OrderStatus.DELIVERED,
                    onSuccess = {
                        Toast.makeText(
                            this,
                            "Bạn đã giao hàng thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = { error ->
                        Toast.makeText(
                            this,
                            "Lỗi: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
        binding.ivLocatedFixed.onClickWithScale {
            moveCameraToStartPosition()
        }
        binding.mapView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE,
                MotionEvent.ACTION_POINTER_DOWN -> {
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(true)
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    binding.nestedScrollView.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }

    private fun getOrderFromIntent() {
        val order =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_ORDER, Order::class.java) ?: Order.getMockOrder()
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Parcelable>(EXTRA_ORDER) as? Order ?: Order.getMockOrder()
            }
        viewModel.initializeOrder(order)
    }

    private fun setupToolbar() {
        binding.apply {
            ivBack.onClickWithScale {
                onBackPressedDispatcher.onBackPressed()
            }
            viewModel.order.value?.let { order ->
                tvOrderId.text = order.orderId
            }
        }
    }

    private fun setupOrderInformation(order: Order) {
        binding.apply {
            tvGoodsDes.text = order.orderType.displayName
            tvGoodsWeight.text = order.goodsWeight
            tvDistance.text = order.distance
            tvDeliveryFee.text = "${String.format("%,d", order.income)}đ"
        }
    }

    private fun setupRecyclerView(items: List<DeliveryAddressItem>) {
        binding.apply {
            rvDeliveryAddress.visibility = View.VISIBLE
            rvDeliveryAddress.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
            rvDeliveryAddress.adapter = DeliveryAddressAdapter2(
                items = items,
                orderStatus = viewModel.orderStatus.value,
                onCallClick = { name ->
                    // TODO: Implement call functionality
                },
                onMessageClick = { name ->
                    // TODO: Implement message functionality
                },
                onDeliveredClick = { item ->
                    handleDeliveredClick(item)
                }
            )
        }
    }
    
    private fun handleDeliveredClick(item: DeliveryAddressItem) {
        if (item.isPickup) {
            viewModel.handlePickupAddressClick()
        } else {
            item.packageId?.let { packageId ->
                viewModel.handleDropoffAddressClick(packageId, item.packageStatus)
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

                val startLatLng = viewModel.positioning.value?.let {
                    LatLng(it.coordinates.lat, it.coordinates.lng)
                }

                val currentState = viewModel.currentState.value
                val destinationAddress = if (currentState >= OrderState.RECEIVED_GOODS) {
                    viewModel.getDropOffAddress()
                } else {
                    viewModel.getPickUpAddress()
                }
                val destinationLatLng = LatLng(
                    destinationAddress.coordinates.lat,
                    destinationAddress.coordinates.lng
                )
                val destinationTitle = if (currentState >= OrderState.RECEIVED_GOODS) {
                    "Điểm giao hàng"
                } else {
                    "Điểm lấy hàng"
                }

                startMarker?.remove()
                endMarker?.remove()

                if (startLatLng != null) {
                    startMarker = map.addMarker(
                        MarkerOptions().position(startLatLng).icon(driverIcon)
                            .title("Vị trí hiện tại")
                    )
                }

                endMarker = map.addMarker(
                    MarkerOptions().position(destinationLatLng).icon(endIcon)
                        .title(destinationTitle)
                )

                if (startLatLng != null) {
                    val bounds = LatLngBounds.Builder()
                        .include(startLatLng)
                        .include(destinationLatLng)
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
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15.0))
                }

                viewModel.directionResponse.value?.let { response ->
                    drawRoute(map, response)
                }
            }
        }
    }

    private fun drawRoute(map: MapLibreMap, response: GoongDirectionApiResponse) {
        routePolyline?.remove()

        val route = response.routes?.firstOrNull() ?: return
        val encodedPolyline = route.overview_polyline?.points ?: return

        val decodedPath = decodePolyline(encodedPolyline)

        val lineOptions = PolylineOptions()
            .addAll(decodedPath)
            .width(6f)
            .color(getColor(R.color.main_blue))

        routePolyline = map.addPolyline(lineOptions)
    }

    private fun moveCameraToStartPosition() {
        viewModel.positioning.value?.let { startAddress ->
            val startLatLng = LatLng(startAddress.coordinates.lat, startAddress.coordinates.lng)
            mapLibreMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15.0))
        }
    }

    private fun updateMapForState() {
        mapLibreMap?.let { map ->
            val currentState = viewModel.currentState.value
            val destinationAddress = if (currentState >= OrderState.RECEIVED_GOODS) {
                viewModel.getDropOffAddress()
            } else {
                viewModel.getPickUpAddress()
            }

            val startLatLng = viewModel.positioning.value?.let {
                LatLng(it.coordinates.lat, it.coordinates.lng)
            } ?: return

            val destinationLatLng = LatLng(
                destinationAddress.coordinates.lat,
                destinationAddress.coordinates.lng
            )

            endMarker?.remove()

            val iconFactory = IconFactory.getInstance(this)
            val endBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_map)
                .scale(80, 80, false)
            val endIcon = iconFactory.fromBitmap(endBitmap)

            val destinationTitle = if (currentState >= OrderState.RECEIVED_GOODS) {
                "Điểm giao hàng"
            } else {
                "Điểm lấy hàng"
            }

            endMarker = map.addMarker(
                MarkerOptions().position(destinationLatLng).icon(endIcon).title(destinationTitle)
            )

            val bounds = LatLngBounds.Builder()
                .include(startLatLng)
                .include(destinationLatLng)
                .build()

            binding.mapView.post {
                try {
                    val padding = 150
                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                    map.animateCamera(cameraUpdate)
                } catch (e: Exception) {
                    e.printStackTrace()
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 12.0))
                }
            }

            if (currentState >= OrderState.RECEIVED_GOODS) {
                getDirectionToDropOff()
            } else {
                getDirection()
            }
        }
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
        autoUpdateJob?.cancel()
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
