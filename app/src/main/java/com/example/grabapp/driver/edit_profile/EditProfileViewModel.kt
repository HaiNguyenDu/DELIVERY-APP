package com.example.grabapp.driver.edit_profile

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.example.grabapp.base.BaseViewModel
import com.example.grabapp.model.CCCDInfo
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class EditProfileViewModel(
    application: Application
) : BaseViewModel(application) {

    private val barcodeScanner = BarcodeScanning.getClient()

    private val _cccdInfo = MutableStateFlow<CCCDInfo?>(null)
    val cccdInfo = _cccdInfo.asStateFlow()

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

    private suspend fun scanBarcode(image: InputImage): String? = suspendCancellableCoroutine { continuation ->
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

    override fun onCleared() {
        super.onCleared()
        barcodeScanner.close()
    }
}
