package com.example.grabapp.driver.order_detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
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
import com.example.grabapp.data.OrderStorage
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
import com.example.grabapp.service.MyFirebaseMessagingService
import com.example.grabapp.view.dialog.CancelOrderDialog
import kotlinx.coroutines.Job
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
import androidx.core.net.toUri

class OrderDetailActivity : BaseActivity<ActivityDetailOrderBinding, OrderDetailViewModel>() {

    companion object {
        private const val EXTRA_ORDER = "extra_order"
    }

    private var mapLibreMap: MapLibreMap? = null

    private var startMarker: Marker? = null
    private var endMarker: Marker? = null
    private var routePolyline: Polyline? = null
    private var autoUpdateJob: kotlinx.coroutines.Job? = null
    private var locationUpdateJob: Job? = null
    private lateinit var orderStorage: OrderStorage
    private var isWaitingForCancellation = false
    private var isReturnArrived = false

    private val orderCancelledReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == MyFirebaseMessagingService.ACTION_ORDER_CANCELLED) {
                val orderId = intent.getStringExtra(MyFirebaseMessagingService.EXTRA_ORDER_ID)
                orderId?.let {
                    val currentOrderId =
                        viewModel.order.value?.orderId ?: viewModel.orderResponse.value?.id
                    if (currentOrderId == orderId) {
                        handleOrderCancelledNotification()
                    }
                }
            }
        }
    }

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
        orderStorage = OrderStorage(this)
        registerOrderCancelledReceiver()
        setupClickListeners()
        getOrderFromIntent()
        setupToolbar()
        observeViewModel()
        requestLocationPermission()
        setupAutoUpdate()
    }

    private fun registerOrderCancelledReceiver() {
        val filter = IntentFilter(MyFirebaseMessagingService.ACTION_ORDER_CANCELLED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(orderCancelledReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(orderCancelledReceiver, filter)
        }
    }

    private fun handleOrderCancelledNotification() {
        isWaitingForCancellation = false
        // Reload order details để lấy status mới nhất (ORDER_CANCELLED)
        val orderId = viewModel.orderResponse.value?.id ?: viewModel.order.value?.orderId
        orderId?.let {
            viewModel.loadOrderDetails(it)
        }
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
                    viewModel.orderResponse.collect { orderResponse ->
                        orderResponse?.let {
                            setupPackageImages(it.packages.mapNotNull { packageInfo ->
                                packageInfo.imageUrl
                            })
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
                            if (it == OrderStatus.CANCELLED_BY_DRIVER && !isWaitingForCancellation) {
                                isWaitingForCancellation = true
                            }

                            updateOrderStatusDisplay(it)
                            updateUIForOrderStatus(it)

                            // Xóa orderId khi order hoàn thành hoặc bị hủy
                            if (it == OrderStatus.DELIVERED ||
                                it == OrderStatus.RETURNED ||
                                it == OrderStatus.ORDER_CANCELLED ||
                                it == OrderStatus.CANCELLED_BY_DRIVER ||
                                it == OrderStatus.CANCELLED_BY_SENDER
                            ) {
                                orderStorage.clearActiveOrder()
                                isWaitingForCancellation = false
                            }
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

    private fun startLocationMarkerUpdate() {
        locationUpdateJob?.cancel()
        locationUpdateJob = lifecycleScope.launch {
            while (true) {
                delay(5000)
                updateDriverMarkerPosition()
            }
        }
    }

    private fun stopLocationMarkerUpdate() {
        locationUpdateJob?.cancel()
        locationUpdateJob = null
    }

    private fun updateDriverMarkerPosition() {
        val map = mapLibreMap ?: return
        val currentMarker = startMarker ?: return

        // Lấy vị trí hiện tại
        viewModel.getCurrentLocation(
            onSuccess = { address ->
                val newLatLng = LatLng(
                    address.coordinates.lat,
                    address.coordinates.lng
                )

                // Cập nhật marker position mà không cập nhật camera
                currentMarker.position = newLatLng
            },
            onError = {
                // Không làm gì nếu không lấy được location
            }
        )
    }

    private fun setupAutoUpdate() {
        autoUpdateJob = lifecycleScope.launch {
            delay(10000) // 10 seconds
            val orderStatus = viewModel.orderStatus.value
            if (orderStatus == OrderStatus.DRIVER_ASSIGNED) {
                // Auto update to DRIVER_EN_ROUTE_PICKUP if user hasn't clicked
                viewModel.updateOrderStatus(
                    OrderStatus.DRIVER_EN_ROUTE_PICKUP,
                    null,
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

    private fun updateUIForOrderStatus(status: OrderStatus) {
        when (status) {
            OrderStatus.ORDER_CANCELLED -> {
                binding.tvStatus.text = "Đã Hủy"
                binding.tvDeliveryComplete.text = "Đã Hủy"
                binding.tvCancelOrder.visibility = View.GONE
                isWaitingForCancellation = false
            }

            OrderStatus.CANCELLED_BY_DRIVER -> {
                binding.tvStatus.text = "Đang tìm tài xế"
                binding.tvDeliveryComplete.text = "Đang đợi xác nhận"
                binding.tvCancelOrder.visibility = View.GONE
            }

            OrderStatus.RETURNING_TO_SENDER -> {
                binding.tvStatus.text = "Đang trả hàng"
                binding.tvDeliveryComplete.text = "Đã đến điểm trả hàng"
                binding.tvCancelOrder.visibility = View.GONE
                isReturnArrived = false
                updateMapForReturning()
            }

            OrderStatus.RETURNED -> {
                binding.tvStatus.text = "Đã trả hàng"
                binding.tvDeliveryComplete.text = "Đã trả hàng"
                binding.tvCancelOrder.visibility = View.GONE
            }

            OrderStatus.DELIVERED_WITH_ISSUES -> {
                binding.tvStatus.text = "Giao hàng có sự cố"
                binding.tvDeliveryComplete.text = "Đã trả hàng"
                binding.tvCancelOrder.visibility = View.GONE
            }

            else -> {
                val canCancel = status == OrderStatus.ARRIVED_PICKUP ||
                        status == OrderStatus.DRIVER_EN_ROUTE_PICKUP ||
                        status == OrderStatus.DRIVER_ASSIGNED ||
                        status == OrderStatus.PACKAGE_PICKED ||
                        status == OrderStatus.EN_ROUTE_DELIVERY ||
                        status == OrderStatus.ARRIVED_DELIVERY
                binding.tvCancelOrder.visibility = if (canCancel) View.VISIBLE else View.GONE
            }
        }
    }

    private fun updateMapForReturning() {
        mapLibreMap?.let { map ->
            val startLatLng = viewModel.positioning.value?.let {
                LatLng(it.coordinates.lat, it.coordinates.lng)
            } ?: return

            val pickupAddress = viewModel.getPickUpAddress()
            val pickupLatLng = LatLng(
                pickupAddress.coordinates.lat,
                pickupAddress.coordinates.lng
            )

            endMarker?.remove()
            val iconFactory = IconFactory.getInstance(this)
            val endBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_map)
                .scale(80, 80, false)
            val endIcon = iconFactory.fromBitmap(endBitmap)

            endMarker = map.addMarker(
                MarkerOptions().position(pickupLatLng).icon(endIcon)
                    .title("Điểm trả hàng")
            )

            viewModel.getDirectionToAddress(
                destination = pickupAddress,
                onSuccess = { response ->
                    drawRoute(map, response)
                },
                onError = { error ->
                }
            )

            val bounds = LatLngBounds.Builder()
                .include(startLatLng)
                .include(pickupLatLng)
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
        }
    }

    private fun showCancelOrderDialog() {
        val currentStatus = viewModel.orderStatus.value ?: return
        CancelOrderDialog.newInstance(
            orderStatus = currentStatus,
            onApplyClick = { cancelType ->
                // Set flag để hiển thị trạng thái đang đợi xác nhận
                isWaitingForCancellation = true
                viewModel.cancelOrder(
                    cancelType,
                    onSuccess = {
                        // Update UI với trạng thái đang đợi xác nhận
                        val currentStatusAfterCancel = viewModel.orderStatus.value
                        if (currentStatusAfterCancel == OrderStatus.CANCELLED_BY_DRIVER) {
                            updateUIForOrderStatus(OrderStatus.CANCELLED_BY_DRIVER)
                        }
                        Toast.makeText(
                            this@OrderDetailActivity,
                            "Đang đợi xác nhận hủy đơn",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = { error ->
                        isWaitingForCancellation = false
                        Toast.makeText(
                            this@OrderDetailActivity,
                            "Lỗi: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        ).show(supportFragmentManager, "CancelOrderDialog")
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
        binding.tvCancelOrder.onClickWithScale {
            showCancelOrderDialog()
        }

        binding.tvDeliveryComplete.onClickWithScale {
            if (binding.tvDeliveryComplete.isEnabled) {
                val currentStatus = viewModel.orderStatus.value
                if (currentStatus == OrderStatus.RETURNING_TO_SENDER) {
                    if (!isReturnArrived) {
                        // Lần đầu nhấn: đổi text thành "Trả hàng thành công"
                        binding.tvDeliveryComplete.text = "Trả hàng thành công"
                        isReturnArrived = true
                    } else {
                        // Lần thứ hai nhấn: complete return
                        viewModel.completeReturnWithIssues(
                            onSuccess = {
                                Toast.makeText(
                                    this,
                                    "Đã trả hàng thành công",
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
                } else {
                    viewModel.updateOrderStatus(
                        OrderStatus.DELIVERED,
                        null,
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
        orderStorage.saveActiveOrderId(order.orderId)
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
                onCallClick = { item ->
                    handleCallClick(item)
                },
                onMessageClick = { name ->
                    Toast.makeText(
                        this@OrderDetailActivity,
                        "Chức năng đang được phát triển",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onDeliveredClick = { item ->
                    handleDeliveredClick(item)
                },
                onCancelClick = { item ->
                    handleCancelClick(item)
                }
            )
        }
    }

    private fun handleCancelClick(item: DeliveryAddressItem) {
        if (!item.isPickup) {
            item.packageId?.let { packageId ->
                viewModel.handleCancelPackageClick(packageId)
            }
        }
    }

    private fun handleCallClick(item: DeliveryAddressItem) {
        val phoneNumber = item.phone
        if (phoneNumber.isNullOrEmpty()) {
            Toast.makeText(this, "Không có số điện thoại", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể mở ứng dụng gọi điện", Toast.LENGTH_SHORT).show()
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

    private fun setupPackageImages(imageUrls: List<String>) {
        binding.apply {
            if (imageUrls.isNotEmpty()) {
                rvPackageImage.visibility = View.VISIBLE
                rvPackageImage.layoutManager = LinearLayoutManager(
                    this@OrderDetailActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                rvPackageImage.adapter = PackageImageAdapter(imageUrls)
            } else {
                rvPackageImage.visibility = View.GONE
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

                    // Bắt đầu cập nhật location marker sau khi map đã được setup
                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        startLocationMarkerUpdate()
                    }
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
        if (mapLibreMap != null && startMarker != null) {
            startLocationMarkerUpdate()
        }
    }

    override fun onPause() {
        stopLocationMarkerUpdate()
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        stopLocationMarkerUpdate()
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        autoUpdateJob?.cancel()
        stopLocationMarkerUpdate()
        try {
            unregisterReceiver(orderCancelledReceiver)
        } catch (e: Exception) {
        }
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
