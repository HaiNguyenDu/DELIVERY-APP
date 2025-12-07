package com.example.grabapp.util

import com.example.grabapp.data.model.OrderResponse
import com.example.grabapp.model.Order
import com.example.grabapp.model.OrderState
import com.example.grabapp.model.OrderType
import com.example.grabapp.respone.Coordinates
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.ceil

object OrderMapper {

    private val isoDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun mapToOrder(orderResponse: OrderResponse): Order {
        val orderState = mapStatusToOrderState(orderResponse.status)
        val formattedDate = formatDate(orderResponse.createdAt)
        val distance = formatDistance(orderResponse.priceAndRoutes)

        val dropoffAddress = orderResponse.packages.firstOrNull()?.dropoffAddress?.detail
            ?: orderResponse.pickupAddress.detail

        val pickupName = orderResponse.pickupAddress.name
        val dropoffName = orderResponse.packages.firstOrNull()?.dropoffAddress?.name
            ?: pickupName

        val dropoffNote = orderResponse.packages.firstOrNull()?.dropoffAddress?.note

        val pickupCoordinates = Coordinates(
            lat = orderResponse.pickupAddress.latitude,
            lng = orderResponse.pickupAddress.longitude
        )
        
        val dropoffCoordinates = orderResponse.packages.firstOrNull()?.dropoffAddress?.let {
            Coordinates(
                lat = it.latitude,
                lng = it.longitude
            )
        }

        return Order(
            orderId = orderResponse.id,
            pickerName = pickupName,
            pickerAddress = orderResponse.pickupAddress.detail,
            deliveryName = dropoffName,
            deliveryAddress = dropoffAddress,
            distance = distance,
            estimatedTime = formatEstimatedTime(orderResponse.priceAndRoutes),
            income = orderResponse.totalAmount,
            hasCOD = orderResponse.packages.any { it.codFee > 0 },
            notion = orderResponse.pickupAddress.note,
            dropoffNote = dropoffNote,
            fragileGoods = false, // Not available in API response
            isBusinessHours = true, // Not available in API response
            orderType = OrderType.OTHER,
            orderState = orderState,
            goodsWeight = formatWeight(orderResponse.packages),
            orderTime = formattedDate,
            pickupCoordinates = pickupCoordinates,
            dropoffCoordinates = dropoffCoordinates
        )
    }

    private fun mapStatusToOrderState(status: String): OrderState {
        return when (status.uppercase()) {
            "DELIVERED" -> OrderState.DELIVERED
            "DRIVER_EN_ROUTE_PICKUP",
            "ARRIVED_PICKUP",
            "PACKAGE_PICKED",
            "EN_ROUTE_DELIVERY" -> OrderState.DELIVERING

            "CANCELLED_BY_SENDER",
            "CANCELLED_BY_DRIVER",
            "ORDER_CANCELLED",
            "DELIVERY_FAILED",
            "RETURNED",
            "PICKUP_FAILED" -> OrderState.CANCELED

            else -> OrderState.RECEIVED_ORDER
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val formats = listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            )

            var parsedDate: java.util.Date? = null
            for (format in formats) {
                try {
                    parsedDate = format.parse(dateString)
                    if (parsedDate != null) break
                } catch (e: Exception) {
                }
            }

            parsedDate?.let { displayDateFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    private fun formatDistance(priceAndRoutes: List<com.example.grabapp.data.model.PriceAndRoute>): String {
        if (priceAndRoutes.isEmpty()) return "0 km"

        val totalDistance = priceAndRoutes.sumOf { it.distance }

        return when {
            totalDistance < 1000 -> "${totalDistance}m"
            else -> {
                val km = totalDistance / 1000.0
                String.format(Locale.getDefault(), "%.1f km", km).replace(".", ",")
            }
        }
    }

    private fun formatEstimatedTime(priceAndRoutes: List<com.example.grabapp.data.model.PriceAndRoute>): String {
        if (priceAndRoutes.isEmpty()) return "0 phút"

        val totalSeconds = priceAndRoutes.sumOf { it.estimatedDuration }
        val minutes = (totalSeconds / 60).toInt()

        return if (minutes < 60) {
            "$minutes phút"
        } else {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (remainingMinutes == 0) {
                "$hours giờ"
            } else {
                "$hours giờ $remainingMinutes phút"
            }
        }
    }

    private fun formatWeight(packages: List<com.example.grabapp.data.model.PackageInfo>): String {
        if (packages.isEmpty()) return "0 kg"

        val totalWeight = packages.sumOf { it.weightKg }
        return if (totalWeight % 1 == 0.0) {
            "${totalWeight.toInt()} kg"
        } else {
            String.format(Locale.getDefault(), "%.1f kg", totalWeight)
        }
    }

    fun isDeliveringStatus(status: String): Boolean {
        return when (status.uppercase()) {
            "DRIVER_EN_ROUTE_PICKUP",
            "ARRIVED_PICKUP",
            "PACKAGE_PICKED",
            "EN_ROUTE_DELIVERY" -> true

            else -> false
        }
    }

    fun isCanceledStatus(status: String): Boolean {
        return when (status.uppercase()) {
            "CANCELLED_BY_SENDER",
            "CANCELLED_BY_DRIVER",
            "ORDER_CANCELLED",
            "DELIVERY_FAILED",
            "RETURNED",
            "PICKUP_FAILED" -> true

            else -> false
        }
    }
    
    /**
     * Format distance cho dialog: từ mét sang km với 2 số thập phân
     * Ví dụ: 1234m -> 1.23km
     */
    fun formatDistanceForDialog(priceAndRoutes: List<com.example.grabapp.data.model.PriceAndRoute>): String {
        if (priceAndRoutes.isEmpty()) return "0.00 km"

        val totalDistance = priceAndRoutes.sumOf { it.distance }
        val km = totalDistance / 1000.0
        return String.format(Locale.getDefault(), "%.2f km", km)
    }
    
    /**
     * Format time cho dialog: từ giây sang phút, số tròn (không thập phân)
     * Ví dụ: 1006 giây -> 17 phút (làm tròn lên)
     */
    fun formatTimeForDialog(priceAndRoutes: List<com.example.grabapp.data.model.PriceAndRoute>): String {
        if (priceAndRoutes.isEmpty()) return "0 phút"

        val totalSeconds = priceAndRoutes.sumOf { it.estimatedDuration }
        val minutes = ceil(totalSeconds / 60.0).toInt() // Làm tròn lên
        
        return "$minutes phút"
    }
}

