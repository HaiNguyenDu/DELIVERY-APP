package com.example.grabapp.driver.edit_profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityEditProfileBinding
import com.example.grabapp.extention.onClickWithScale
import java.io.InputStream

class EditProfileActivity : BaseActivity<ActivityEditProfileBinding, EditProfileViewModel>() {
    
    private val pickAvatarLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it, ImageType.AVATAR) }
    }

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

    override fun getLazyBinding(): Lazy<ActivityEditProfileBinding> =
        lazy { ActivityEditProfileBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<EditProfileViewModel> =
        lazy { EditProfileViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            ivUploadAvatar.onClickWithScale {
                openImagePicker(ImageType.AVATAR)
            }
            ivAvatar.onClickWithScale {
                openImagePicker(ImageType.AVATAR)
            }
            ivFrontCard.onClickWithScale {
                openImagePicker(ImageType.FRONT_CARD)
            }
            ivBackCard.onClickWithScale {
                openImagePicker(ImageType.BACK_CARD)
            }
        }
    }

    private fun openImagePicker(imageType: ImageType) {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()

        when (imageType) {
            ImageType.AVATAR -> pickAvatarLauncher.launch(request)
            ImageType.FRONT_CARD -> pickFrontCardLauncher.launch(request)
            ImageType.BACK_CARD -> pickBackCardLauncher.launch(request)
        }
    }

    private fun loadImageFromUri(uri: Uri, imageType: ImageType) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            bitmap?.let {
                when (imageType) {
                    ImageType.AVATAR -> {
                        binding.ivAvatar.setImageBitmap(it)
                    }
                    ImageType.FRONT_CARD -> {
                        binding.ivFrontCard.setImageBitmap(it)
                        binding.ivFrontCard.setPadding(0, 0, 0, 0)
                    }
                    ImageType.BACK_CARD -> {
                        binding.ivBackCard.setImageBitmap(it)
                        binding.ivBackCard.setPadding(0, 0, 0, 0)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private enum class ImageType {
        AVATAR,
        FRONT_CARD,
        BACK_CARD
    }
}
