package com.example.grabapp.driver.register.avatar

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentAvatarBinding
import com.example.grabapp.driver.register.RegisterViewModel
import com.example.grabapp.extention.onClickWithScale
import java.io.InputStream

class AvatarFragment : BaseFragment<FragmentAvatarBinding, RegisterViewModel>() {

    private val pickVisualMediaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { loadImageFromUri(it) }
    }

    override fun getLazyBinding(): Lazy<FragmentAvatarBinding> =
        lazy { FragmentAvatarBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<RegisterViewModel> =
        lazy { RegisterViewModel(requireActivity().application) }

    override fun setUpClick() {
        binding.ivUploadPhoto.onClickWithScale {
            openImagePicker()
        }
        binding.ivAvatar.onClickWithScale {
            openImagePicker()
        }
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(0, 0, 0, 0)
    }

    private fun openImagePicker() {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()
        pickVisualMediaLauncher.launch(request)
    }

    private fun loadImageFromUri(uri: Uri) {
        try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            bitmap?.let {
                binding.apply {
                    ivUploadPhoto.visibility = View.GONE
                    ivAvatar.visibility = View.VISIBLE
                    ivAvatar.setImageBitmap(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
