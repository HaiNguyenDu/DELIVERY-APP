package com.example.grabapp.driver.register.vehicle_confirm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentVehicleConfirmBinding
import com.example.grabapp.driver.register.RegisterViewModel
import com.example.grabapp.extention.onClickWithScale
import java.io.InputStream

class VehicleConfirmFragment : BaseFragment<FragmentVehicleConfirmBinding, RegisterViewModel>() {

    private val pickFrontSideLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, VehicleSide.FRONT) }
    }

    private val pickBackSideLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, VehicleSide.BACK) }
    }

    private val pickRightSideLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, VehicleSide.RIGHT) }
    }

    private val pickLeftSideLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, VehicleSide.LEFT) }
    }

    private enum class VehicleSide {
        FRONT, BACK, RIGHT, LEFT
    }

    override fun getLazyBinding(): Lazy<FragmentVehicleConfirmBinding> =
        lazy { FragmentVehicleConfirmBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { ViewModelProvider(requireActivity())[RegisterViewModel::class.java] }

    override fun setUpClick() {
        binding.apply {
            ivFrontSide.onClickWithScale {
                openImagePicker(VehicleSide.FRONT)
            }

            ivBackSide.onClickWithScale {
                openImagePicker(VehicleSide.BACK)
            }

            ivRightSide.onClickWithScale {
                openImagePicker(VehicleSide.RIGHT)
            }

            ivLeftSide.onClickWithScale {
                openImagePicker(VehicleSide.LEFT)
            }
        }
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }

    private fun openImagePicker(side: VehicleSide) {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()

        when (side) {
            VehicleSide.FRONT -> pickFrontSideLauncher.launch(request)
            VehicleSide.BACK -> pickBackSideLauncher.launch(request)
            VehicleSide.RIGHT -> pickRightSideLauncher.launch(request)
            VehicleSide.LEFT -> pickLeftSideLauncher.launch(request)
        }
    }

    private fun loadImageFromUri(uri: Uri, side: VehicleSide) {
        try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            bitmap?.let {
                when (side) {
                    VehicleSide.FRONT -> {
                        viewModel.vehicleConfirm.frontSideBitmap = it
                        binding.ivFrontSide.setImageBitmap(it)
                        binding.ivFrontSide.setPadding(0, 0, 0, 0)
                    }
                    VehicleSide.BACK -> {
                        viewModel.vehicleConfirm.backSideBitmap = it
                        binding.ivBackSide.setImageBitmap(it)
                        binding.ivBackSide.setPadding(0, 0, 0, 0)
                    }
                    VehicleSide.RIGHT -> {
                        viewModel.vehicleConfirm.rightSideBitmap = it
                        binding.ivRightSide.setImageBitmap(it)
                        binding.ivRightSide.setPadding(0, 0, 0, 0)
                    }
                    VehicleSide.LEFT -> {
                        viewModel.vehicleConfirm.leftSideBitmap = it
                        binding.ivLeftSide.setImageBitmap(it)
                        binding.ivLeftSide.setPadding(0, 0, 0, 0)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
