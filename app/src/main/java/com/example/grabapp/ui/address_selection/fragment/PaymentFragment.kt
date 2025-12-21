package com.example.grabapp.ui.address_selection.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.graphics.set
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentPaymentBinding
import com.example.grabapp.extention.toMoneyFormat
import com.example.grabapp.ui.address_selection.AddressSelectionViewModel
import com.example.grabapp.utils.CurrentOrder
import com.example.grabapp.view.SnackBarCustom
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentFragment : BaseFragment<FragmentPaymentBinding, AddressSelectionViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentPaymentBinding> = lazy {
        FragmentPaymentBinding.inflate(layoutInflater)
    }

    override fun getLazyViewModel(): Lazy<AddressSelectionViewModel> = lazy {
        ViewModelProvider(requireActivity())[AddressSelectionViewModel::class.java]
    }

    override fun setUpClick() {
    }

    private fun setUpUi() {
        lifecycleScope.launch {
            viewModel.paymentResponse.collect {
                if (it == null) return@collect
                val qrContent = it.paymentResponse?.qrCode
                if (qrContent != null) {
                    val qrBitmap = generateQrCode(qrContent)
                    binding.imgQrCode.setImageBitmap(qrBitmap)
                    binding.tvAmount.text = "Số tiền: "+it.totalAmount.toMoneyFormat()+ "₫"
                }
            }
        }
        lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.lottie.isVisible = it
            }
        }
        binding.btnConfirm.setOnClickListener {
            lifecycleScope.launch {
                viewModel.showLoading()
                val isSuccess = viewModel.isPaid()
                if (isSuccess) {
                    CurrentOrder.setOrderId(viewModel.paymentResponse.value?.orderId!!)
                    SnackBarCustom(
                        view = binding.root,
                        message = getString(R.string.create_order_success),
                        backgroundColor = requireContext().getColor(R.color.white),
                        textColor = requireContext().getColor(R.color.green),
                        bottomMarginDp = 100f,
                    ).show()
                    delay(1000)
                    viewModel.setIsSuccess()
                } else {
                    SnackBarCustom(
                        view = binding.root,
                        message = getString(R.string.paid_fail),
                        backgroundColor = requireContext().getColor(R.color.white),
                        textColor = requireContext().getColor(R.color.green),
                        bottomMarginDp = 100f,
                    ).show()
                }
                viewModel.hideLoading()
            }
        }
        binding.btnCancel.setOnClickListener {
            viewModel.updateStatus()
            viewModel.setIsSuccess()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUi()
    }

    private fun generateQrCode(
        content: String,
        size: Int = 600
    ): Bitmap {
        val bitMatrix = QRCodeWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap[x, y] = if (bitMatrix[x, y]) android.graphics.Color.BLACK
                else android.graphics.Color.WHITE
            }
        }
        return bitmap
    }
}