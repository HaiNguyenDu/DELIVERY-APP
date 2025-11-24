package com.example.grabapp.driver.home.profile

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.databinding.FragmentDriverProfileBinding
import com.example.grabapp.driver.home.DriverHomeViewModel
import com.example.grabapp.driver.home.FaceUploadState
import com.example.grabapp.extention.onClickWithScale
import kotlinx.coroutines.launch

class DriverProfileFragment : BaseFragment<FragmentDriverProfileBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentDriverProfileBinding> =
        lazy { FragmentDriverProfileBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy {
            val fileRepository = FileRepository()
            val aiServiceRepository = AIServiceRepository()
            DriverHomeViewModel(
                requireActivity().application,
                fileRepository,
                aiServiceRepository
            )
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                // TODO: Lấy UUID thực tế của driver từ TokenStorage hoặc UserInfo
                val uid = "driver-uuid-placeholder"
                viewModel.uploadDriverFace(uri, uid)
            } else {
                Toast.makeText(requireContext(), "Không có ảnh nào được chọn", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUploadState()
    }

    override fun setUpClick() {
        binding.tvSummaryName.onClickWithScale {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.emptyView.setPadding(0, inset.top, 0, 0)
        binding.textView1.setPadding(0, inset.top / 2, 0, 0)
    }

    private fun observeUploadState() {
        lifecycleScope.launch {
            viewModel.uploadState.collect { state ->
                when (state) {
                    is FaceUploadState.Idle -> {
                    }
                    is FaceUploadState.PickingImage -> {
                    }
                    is FaceUploadState.UploadingFile -> {
                    }
                    is FaceUploadState.UploadingToAI -> {
                    }
                    is FaceUploadState.Success -> {
                        loadImageFromUrl(state.imageUrl)
                        Toast.makeText(
                            requireContext(),
                            "Upload ảnh thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is FaceUploadState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun loadImageFromUrl(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .centerCrop()
            .into(binding.tvSummaryName)
    }
}
