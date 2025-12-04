package com.example.grabapp.util

import android.content.Context
import android.util.Log
import com.example.grabapp.data.ConnectionStorage
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.data.repository.DriverRepository
import com.example.grabapp.data.model.UpdateDriverLocationRequest
import com.example.grabapp.driver.home.data.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Manager để quản lý việc cập nhật vị trí tài xế định kỳ
 * - Cập nhật vị trí mỗi 10 giây
 * - Chỉ chạy khi connection state là CONNECTED
 * - Hoạt động ở background, không phụ thuộc vào Activity/Fragment
 */
class LocationUpdateManager private constructor(context: Context) {

    private val addressRepository = AddressRepository.getInstance(context)
    private val driverRepository = DriverRepository(context)
    private val connectionStorage = ConnectionStorage(context)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var updateJob: Job? = null

    companion object {
        private const val TAG = "LocationUpdateManager"
        private const val UPDATE_INTERVAL_MS = 10_000L // 10 giây

        @Volatile
        private var INSTANCE: LocationUpdateManager? = null

        fun getInstance(context: Context): LocationUpdateManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationUpdateManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /**
     * Bắt đầu cập nhật vị trí định kỳ
     * Chỉ bắt đầu nếu connection state là CONNECTED
     */
    fun startLocationUpdates() {
        val currentState = connectionStorage.getConnectionState()
        if (currentState != ConnectionState.CONNECTED) {
            Log.d(TAG, "Connection state không phải CONNECTED, không bắt đầu update location")
            return
        }

        if (updateJob?.isActive == true) {
            Log.d(TAG, "Location update đã đang chạy")
            return
        }

        Log.d(TAG, "Bắt đầu cập nhật vị trí định kỳ")
        updateJob = scope.launch {
            while (isActive) {
                try {
                    // Kiểm tra connection state trước mỗi lần update
                    val state = connectionStorage.getConnectionState()
                    if (state != ConnectionState.CONNECTED) {
                        Log.d(TAG, "Connection state không còn CONNECTED, dừng update location")
                        break
                    }

                    // Lấy vị trí hiện tại
                    val location = addressRepository.getCurrentLocation()
                    if (location != null) {
                        val request = UpdateDriverLocationRequest(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )

                        // Gọi API update location
                        when (val result = driverRepository.updateDriverLocation(request)) {
                            is DriverRepository.UpdateLocationResult.Success -> {
                                Log.d(TAG, "Cập nhật vị trí thành công: lat=${location.latitude}, lng=${location.longitude}")
                            }
                            is DriverRepository.UpdateLocationResult.Error -> {
                                Log.e(TAG, "Lỗi cập nhật vị trí: ${result.message}")
                            }
                        }
                    } else {
                        Log.w(TAG, "Không thể lấy vị trí hiện tại")
                    }

                    // Đợi 10 giây trước khi update tiếp
                    delay(UPDATE_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception khi update location: ${e.message}", e)
                    // Tiếp tục vòng lặp ngay cả khi có lỗi
                    delay(UPDATE_INTERVAL_MS)
                }
            }
        }
    }

    /**
     * Dừng cập nhật vị trí
     */
    fun stopLocationUpdates() {
        Log.d(TAG, "Dừng cập nhật vị trí")
        updateJob?.cancel()
        updateJob = null
    }

    /**
     * Kiểm tra xem có đang cập nhật vị trí không
     */
    fun isUpdating(): Boolean {
        return updateJob?.isActive == true
    }

    /**
     * Hủy tất cả và giải phóng tài nguyên
     */
    fun destroy() {
        stopLocationUpdates()
        scope.cancel()
        INSTANCE = null
    }
}

