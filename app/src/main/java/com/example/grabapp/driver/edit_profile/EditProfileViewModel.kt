package com.example.grabapp.driver.edit_profile

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.common.BASE_URL
import com.example.grabapp.data.ProfileStorage
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.data.auth.DriverApi
import com.example.grabapp.data.model.DriverProfile
import com.example.grabapp.data.model.DriverRegisterRequest
import com.example.grabapp.data.repository.DriverRepository
import com.example.grabapp.model.CCCDInfo
import com.example.grabapp.api.RetrofitProvider
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import java.text.SimpleDateFormat
import java.util.Locale

class EditProfileViewModel(
    application: Application
) : BaseViewModel(application) {

    private val barcodeScanner = BarcodeScanning.getClient()
    private val tokenStorage = TokenStorage(application)
    private val profileStorage = ProfileStorage(application)
    private val driverRepository = DriverRepository(application)

    private val _cccdInfo = MutableStateFlow<CCCDInfo?>(null)
    val cccdInfo = _cccdInfo.asStateFlow()

    private val _registerResult = MutableStateFlow<DriverRepository.RegisterResult?>(null)
    val registerResult = _registerResult.asStateFlow()

    fun scanBarcodesFromBitmaps(frontBitmap: Bitmap, backBitmap: Bitmap) {
        viewModelScope.launch {
            showLoading()
            try {
                val frontImage = InputImage.fromBitmap(frontBitmap, 0)
                var result = scanBarcode(frontImage)

                if (result == null) {
                    val backImage = InputImage.fromBitmap(backBitmap, 0)
                    result = scanBarcode(backImage)
                }

                result?.let { qrString ->
                    val cccdInfo = CCCDInfo.parseFromQRString(qrString)
                    _cccdInfo.value = cccdInfo
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                hideLoading()
            }
        }
    }

    private suspend fun scanBarcode(image: InputImage): String? =
        suspendCancellableCoroutine { continuation ->
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        when (barcode.valueType) {
                            Barcode.TYPE_TEXT -> {
                                val rawValue = barcode.rawValue
                                if (rawValue != null && rawValue.contains("|")) {
                                    continuation.resume(rawValue)
                                    return@addOnSuccessListener
                                }
                            }

                            else -> {}
                        }
                    }
                    continuation.resume(null)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    continuation.resume(null)
                }
        }

    fun registerDriver(
        vehiclePlateNumber: String,
        licenseNumber: String,
        identityFullName: String,
        identityNumber: String,
        identityIssueDate: Long,
        identityIssuePlace: String,
        identityAddress: String,
        identityGender: String,
        identityBirthdate: Long,
        vehicleType: String
    ) {
        viewModelScope.launch {
            showLoading()
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val request = DriverRegisterRequest(
                    vehiclePlateNumber = vehiclePlateNumber,
                    licenseNumber = licenseNumber,
                    identityFullName = identityFullName,
                    identityNumber = identityNumber,
                    identityIssueDate = dateFormat.format(identityIssueDate),
                    identityIssuePlace = identityIssuePlace,
                    identityAddress = identityAddress,
                    identityGender = convertGenderToApiFormat(identityGender),
                    identityBirthdate = dateFormat.format(identityBirthdate)
                )

                val result = driverRepository.register(request)
                _registerResult.value = result

                if (result is DriverRepository.RegisterResult.Success) {
                    val profile = DriverProfile(
                        name = identityFullName,
                        cccdNumber = identityNumber,
                        licenseNumber = licenseNumber,
                        birthDate = identityBirthdate,
                        gender = identityGender,
                        issueDate = identityIssueDate,
                        issuePlace = identityIssuePlace,
                        address = identityAddress,
                        vehicleType = vehicleType,
                        vehiclePlate = vehiclePlateNumber,
                        phone = tokenStorage.getPhone()
                    )
                    profileStorage.saveProfile(profile)
                    profileStorage.setPendingStatus(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _registerResult.value =
                    DriverRepository.RegisterResult.Error(null, e.message ?: "Unexpected error")
            } finally {
                hideLoading()
            }
        }
    }

    private fun convertGenderToApiFormat(gender: String): String {
        return when (gender) {
            "Nam" -> "MALE"
            "Nữ" -> "FEMALE"
            else -> "MALE"
        }
    }

    fun getSavedProfile(): DriverProfile? {
        return profileStorage.getProfile()
    }

    fun isPending(): Boolean {
        return profileStorage.isPending()
    }

    override fun onCleared() {
        super.onCleared()
        barcodeScanner.close()
    }
}
