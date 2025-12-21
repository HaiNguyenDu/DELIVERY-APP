package com.example.grabapp.driver.home.profile

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.data.repository.OrderRepository
import com.example.grabapp.databinding.FragmentDriverProfileBinding
import com.example.grabapp.driver.edit_profile.EditProfileActivity
import com.example.grabapp.driver.home.DriverHomeViewModel
import com.example.grabapp.driver.home.DriverHomeViewModelFactory
import com.example.grabapp.driver.home.FaceUploadState
import com.example.grabapp.driver.setting.SettingActivity
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity
import kotlinx.coroutines.launch

class DriverProfileFragment : BaseFragment<FragmentDriverProfileBinding, DriverHomeViewModel>() {
    private val tokenStorage by lazy { TokenStorage(requireContext()) }

    override fun getLazyBinding(): Lazy<FragmentDriverProfileBinding> =
        lazy { FragmentDriverProfileBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy {
            val fileRepository = FileRepository()
            val aiServiceRepository = AIServiceRepository()
            val orderRepository = OrderRepository(requireContext())
            val factory = DriverHomeViewModelFactory(
                requireActivity().application,
                fileRepository,
                aiServiceRepository,
                orderRepository
            )
            ViewModelProvider(requireActivity(), factory)[DriverHomeViewModel::class.java]
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val userId = tokenStorage.getUserId()
                if (userId != null) {
                    viewModel.uploadDriverFace(uri, userId)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Không tìm thấy thông tin người dùng.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Không có ảnh nào được chọn", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvPhone.text = tokenStorage.getPhone()
        observeUploadState()
        observeViewModel()
        viewModel.fetchOrders()
        viewModel.fetchDriverInfo()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.orders.collect { orders ->
                updateStatistics()
            }
        }

        lifecycleScope.launch {
            viewModel.driverInfo.collect { driverInfo ->
                driverInfo?.let {
                    updateDriverInfo(it)
                }
            }
        }
    }

    private fun updateStatistics() {
        val totalOrders = viewModel.orders.value.size
        val totalCompletedIncome = viewModel.getTotalCompletedIncome()

        binding.apply {
            tvTotalOrders.text = totalOrders.toString()
            tvOrderCount.text = "($totalOrders đơn)"
            tvTotalIncome.text =
                com.example.grabapp.util.CurrencyFormatter.formatIncome(totalCompletedIncome)
        }
    }

    private fun updateDriverInfo(driverInfo: com.example.grabapp.data.model.DriverRegisterResponse) {
        binding.apply {
            tvTotalRate.text = String.format("%.1f", driverInfo.ratingAvg)
            tvRate.text = String.format("%.1f", driverInfo.ratingAvg)

            val isApproved = driverInfo.status.uppercase() == "APPROVED"
            tvDriverStatus.text = if (isApproved) {
                getString(R.string.t_i_x_ch_nh_th_c)
            } else {
                "Đang chờ phê duyệt"
            }
        }
    }

    override fun setUpClick() {
        binding.apply {
            binding.tvSummaryName.onClickWithScale {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            llSetting.onClickWithScale {
                requireContext().startActivity<SettingActivity>()
            }
            tvPhone.onClickWithScale {
                requireContext().startActivity<EditProfileActivity>()
            }
            llLogout.onClickWithScale {

            }
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
