package com.example.grabapp.ui.order

import OrderStatus
import android.animation.ValueAnimator
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.grabapp.R
import com.example.grabapp.data.local.AppDatabase
import com.example.grabapp.data.model.order.DriverResponse
import com.example.grabapp.data.model.payment.PaymentStatusEnum
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.databinding.DialogBottomOrderPlacedBinding
import com.example.grabapp.extention.getTime
import com.example.grabapp.extention.toMoneyFormat
import com.example.grabapp.respone.GoongDirectionApiResponse
import com.example.grabapp.ui.home.MainViewModel
import com.example.grabapp.ui.home.MainViewModelFactory
import com.example.grabapp.ui.login.DetailOrderItemHistoryAdapter
import com.example.grabapp.ui.login.DetailOrderItemHistoryAdapter.DetailOrderItemAdapterListener
import com.example.grabapp.utils.CurrentOrder
import com.example.grabapp.view.PreviewImageDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class OrderPlacedDialog : BottomSheetDialogFragment() {
    private var _binding: DialogBottomOrderPlacedBinding? = null
    private val binding get() = _binding!!

    private var driverInfo: DriverResponse? = null
    fun add30Minutes(time: String): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val localTime = LocalTime.parse(time, formatter)
        val newTime = localTime.plusMinutes(30)
        return newTime.format(formatter)
    }

    private fun observerView() {
        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        viewModel.isLoop = false
    }

    private fun observerData() {
        lifecycleScope.launch {
            viewModel.currentOrder.collect {
                if (it == null) return@collect
                if (it.status != OrderStatus.FINDING_DRIVER) {
                    handleDriverInfo(it.shipperId ?: "")
                }
                val detailOrderItemAdapter =
                    DetailOrderItemHistoryAdapter(it.packages)
                detailOrderItemAdapter.setListener(object : DetailOrderItemAdapterListener {
                    override fun onAddressClick(position: Int) {

                    }

                    override fun onDeleteClick(position: Int) {

                    }

                    override fun onDetailPackageClick(position: Int) {

                    }

                    override fun onPreviewImageClick(url: String) {
                        PreviewImageDialog.with(requireContext(), url).show()
                    }

                })

                binding.rcvOrder.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    adapter = detailOrderItemAdapter
                    isNestedScrollingEnabled = false
                }
                val timeStart = it.createdAt.getTime()
                val timeEnd = add30Minutes(timeStart)
                binding.tvTimeRange.text = "$timeStart - $timeEnd"
                binding.tvAmount.text = it.totalAmount.toMoneyFormat() + "₫"
                if (it.paymentStatus == PaymentStatusEnum.PAID)
                    binding.paymentType.text = "Đã thanh toán"
                else
                    binding.paymentType.text = "Chưa thành toán"
                    binding.paymentType.text = "Chưa thành toán"
                it.let {
                    binding.tvStatusText.text = it.status?.label ?: ""
                    when (it.status) {
                        OrderStatus.FINDING_DRIVER -> setLineStatus(binding.line1, true, false)
                        OrderStatus.DRIVER_ASSIGNED -> {
                            setLineStatus(binding.line1, true, false)
                        }

                        OrderStatus.DRIVER_EN_ROUTE_PICKUP -> {
                            setLineStatus(binding.line1, false, true)
                            setLineStatus(binding.line2, true, false)
                            binding.ivStep3.imageTintList = ContextCompat.getColorStateList(
                                requireContext(), R.color.green
                            )
                        }

                        OrderStatus.PACKAGE_PICKED -> {
                            setLineStatus(binding.line1, false, true)
                            setLineStatus(binding.line2, false, true)
                            setLineStatus(binding.line3, true, false)
                            binding.ivStep4.imageTintList = ContextCompat.getColorStateList(
                                requireContext(), R.color.green
                            )
                        }

                        OrderStatus.DELIVERED -> {
                            dismiss()
                            CurrentOrder.setOrderId("")
                        }

                        OrderStatus.ORDER_CANCELLED,
                        OrderStatus.RETURNED,
                        OrderStatus.DELIVERY_FAILED,
                        OrderStatus.PICKUP_FAILED -> {
                            dismiss()
                        }

                        else -> {
                            setLineStatus(binding.line1, false, true)
                            setLineStatus(binding.line2, false, true)
                            setLineStatus(binding.line3, true, false)
                            binding.ivStep4.imageTintList = ContextCompat.getColorStateList(
                                requireContext(), R.color.green
                            )
                        }
                    }

                }
            }
        }
    }

    suspend fun handleDriverInfo(diverId: String) {
        if (binding.layoutInfoDriver.isVisible) return
        val driverInfo = viewModel.getDriverInfo(diverId) ?: return
        binding.layoutInfoDriver.isVisible = true
        binding.tvDriverName.text = driverInfo.identityFullName
        binding.tvTagSaver.text = "Đ{}"
        if (driverInfo.avatarUrl?.isNotEmpty() ?: false)
            Glide.with(requireContext()).load(driverInfo.avatarUrl).into(binding.ivAvatar)
        binding.tvVehicleInfo.text = driverInfo.vehiclePlateNumber.toString()
        binding.tvRating.text = "${driverInfo.ratingAvg} ★"
        this.driverInfo = driverInfo
        initMap()
        binding.mapView.visibility = View.VISIBLE
        ValueAnimator.ofInt(0, 1000).apply {
            duration = 1000
            addUpdateListener { animator ->
                val paddingTop = animator.animatedValue as Int
                binding.scrollView.setPadding(
                    binding.scrollView.paddingLeft,
                    paddingTop,
                    binding.scrollView.paddingRight,
                    binding.scrollView.paddingBottom
                )
            }
            start()
        }
        binding.scrollView.setBackgroundColor(Color.TRANSPARENT)
    }

    private var mapLibreMap: MapLibreMap? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        MapLibre.getInstance(requireContext())
        _binding = DialogBottomOrderPlacedBinding.inflate(inflater, container, false)
        val userDao = AppDatabase.getInstance(requireContext()).userDao()
        val factory = MainViewModelFactory(userDao, requireActivity().application)
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
        MapLibre.getInstance(requireContext())
        viewModel.isLoop = true
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.isLoop = false
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
                dialogInterface.window?.let { window ->
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFullHeight()
        observerData()
        handleIntent()
        observerView()
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

        lifecycleScope.launch {
            viewModel.directionResponses.collect { responses ->
                if (responses == null) return@collect
                val iconFactory = IconFactory.getInstance(requireContext())
                val pickUp = viewModel.driverLocationResponse ?: return@collect
                val startIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_delivery)
                    .scale(80, 80, false)
                    .let { iconFactory.fromBitmap(it) }
                map.clearAll()

                val startLatLng = LatLng(pickUp.latitude, pickUp.longitude)
                map.addMarker(MarkerOptions().position(startLatLng).icon(startIcon))

                val boundsBuilder = LatLngBounds.Builder().include(startLatLng)
                val listRoute = viewModel.getListRoute()
                listRoute.forEach { item ->
                    val latLng = LatLng(item.latitude, item.longitude)
                    val numberIcon =
                        iconFactory.fromBitmap(getMarkerBitmap(item.packageIndex.toString()))
                    map.addMarker(MarkerOptions().position(latLng).icon(numberIcon))
                    boundsBuilder.include(latLng)
                }

                if (listRoute.isNotEmpty()) {

                    val screenHeight = resources.displayMetrics.heightPixels

                    val topPadding = 0
                    val bottomPadding = screenHeight * 2 / 3
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            boundsBuilder.build(),
                            50,
                            200,
                            50,
                            bottomPadding
                        )
                    )
                }

                responses?.forEach { drawRoute(map, it) }
            }
        }
    }

    private fun handleIntent() {
        val orderId = arguments?.getString("orderId") ?: CurrentOrder.orderID.value
        viewModel.startPolling(orderId)
    }

    private fun setLineStatus(
        progressBar: ProgressBar, isRunning: Boolean, isCompleted: Boolean = true
    ) {
        if (isRunning) {
            progressBar.isIndeterminate = true
            progressBar.indeterminateTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.green)
        } else {
            progressBar.isIndeterminate = false
            progressBar.max = 100

            if (isCompleted) {
                progressBar.progress = 100
                progressBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.green)
            } else {
                progressBar.progress = 100
                progressBar.progressTintList =
                    ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
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

    companion object {
        fun getInstance(orderId: String): OrderPlacedDialog {
            val fragment = OrderPlacedDialog()
            val args = Bundle()
            args.putString("orderId", orderId)
            fragment.arguments = args
            return fragment
        }
    }
}