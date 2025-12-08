package com.example.grabapp.driver.driver_document

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverDocumentBinding
import com.example.grabapp.driver.base.BaseDriverActivity
import com.example.grabapp.extention.onClickWithScale
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DriverDocumentActivity :
    BaseDriverActivity<ActivityDriverDocumentBinding, DriverDocumentViewModel>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val pickFrontCardLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, ImageType.FRONT_CARD) }
    }

    private val pickBackCardLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, ImageType.BACK_CARD) }
    }

    private val pickLicensePlateLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, ImageType.LICENSE_PLATE) }
    }

    private val pickVehicleLicenseLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, ImageType.VEHICLE_LICENSE) }
    }

    override fun getLazyBinding(): Lazy<ActivityDriverDocumentBinding> =
        lazy { ActivityDriverDocumentBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverDocumentViewModel> =
        lazy { DriverDocumentViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setupClickListener()
    }

    override fun handleInsets(v: View, insets: Insets) {
        binding.root.setPadding(0, -insets.top, 0, 0)
    }

    private fun setupClickListener() {
        binding.apply {
            flBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            ivFrontCard.onClickWithScale {
                openImagePicker(ImageType.FRONT_CARD)
            }

            ivBackCard.onClickWithScale {
                openImagePicker(ImageType.BACK_CARD)
            }

            ivLicensePlateUpload.onClickWithScale {
                openImagePicker(ImageType.LICENSE_PLATE)
            }

            ivVehicleLicenseUpload.onClickWithScale {
                openImagePicker(ImageType.VEHICLE_LICENSE)
            }
        }
    }

    private fun openImagePicker(imageType: ImageType) {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()

        when (imageType) {
            ImageType.FRONT_CARD -> pickFrontCardLauncher.launch(request)
            ImageType.BACK_CARD -> pickBackCardLauncher.launch(request)
            ImageType.LICENSE_PLATE -> pickLicensePlateLauncher.launch(request)
            ImageType.VEHICLE_LICENSE -> pickVehicleLicenseLauncher.launch(request)
        }
    }

    private fun loadImageFromUri(uri: Uri, imageType: ImageType) {
        val requestOption = RequestOptions()
            .override(1200)
            .centerCrop()

        when (imageType) {
            ImageType.FRONT_CARD -> {
                Glide.with(this)
                    .load(uri)
                    .apply(requestOption)
                    .into(binding.ivFrontCard)
                binding.ivFrontCard.setPadding(0, 0, 0, 0)
                updateUploadDate(ImageType.FRONT_CARD)
            }

            ImageType.BACK_CARD -> {
                Glide.with(this)
                    .load(uri)
                    .apply(requestOption)
                    .into(binding.ivBackCard)
                binding.ivBackCard.setPadding(0, 0, 0, 0)
                updateUploadDate(ImageType.BACK_CARD)
            }

            ImageType.LICENSE_PLATE -> {
                Glide.with(this)
                    .load(uri)
                    .apply(requestOption)
                    .into(binding.ivLicensePlateUpload)
                binding.ivLicensePlateUpload.setPadding(0, 0, 0, 0)
                updateUploadDate(ImageType.LICENSE_PLATE)
            }

            ImageType.VEHICLE_LICENSE -> {
                Glide.with(this)
                    .load(uri)
                    .apply(requestOption)
                    .into(binding.ivVehicleLicenseUpload)
                binding.ivVehicleLicenseUpload.setPadding(0, 0, 0, 0)
                updateUploadDate(ImageType.VEHICLE_LICENSE)
            }
        }
    }

    private fun updateUploadDate(imageType: ImageType) {
        val currentDate = Calendar.getInstance().time
        val dateText = "Tải lên: ${dateFormat.format(currentDate)}"

        when (imageType) {
            ImageType.FRONT_CARD, ImageType.BACK_CARD -> {
                binding.tvUploadedDate.text = dateText
            }

            ImageType.LICENSE_PLATE -> {
                binding.tvLicensePlateUploadedDate.text = dateText
            }

            ImageType.VEHICLE_LICENSE -> {
                binding.tvVehicleLicenseUploadedDate.text = dateText
            }
        }
    }

    private enum class ImageType {
        FRONT_CARD,
        BACK_CARD,
        LICENSE_PLATE,
        VEHICLE_LICENSE
    }
}
