package com.example.grabapp.driver.home.profile

import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentDriverProfileBinding
import com.example.grabapp.driver.home.DriverHomeViewModel
import com.example.grabapp.extention.onClickWithScale

class DriverProfileFragment : BaseFragment<FragmentDriverProfileBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentDriverProfileBinding> =
        lazy { FragmentDriverProfileBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy { DriverHomeViewModel(requireActivity().application) }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val uriString = uri.toString()
                val uid = "driver-uuid-placeholder"

                viewModel.uploadDriverFace(uriString, uid)
            } else {
                Toast.makeText(requireContext(), "Không có ảnh nào được chọn", Toast.LENGTH_SHORT)
                    .show()
            }
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
}
