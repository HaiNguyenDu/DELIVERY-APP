package com.example.grabapp.ui.login.fragment

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentConfirmOtpBinding
import com.example.grabapp.ui.home.MainActivity
import com.example.grabapp.ui.login.LoginViewModel
import com.example.grabapp.ui.login.adapter.LoginPageAdapter
import com.example.grabapp.view.ExitConfirmDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConfirmOTPFragment : BaseFragment<FragmentConfirmOtpBinding, LoginViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentConfirmOtpBinding> =
        lazy { FragmentConfirmOtpBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<LoginViewModel> =
        lazy { ViewModelProvider(requireActivity())[LoginViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        handleInset()
        observerData()
    }

    private var countdownJob: Job? = null

    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private fun setUpUi() {
        val phone = viewModel.getPhoneNumber()
        if (phone.isEmpty()) return
        val textBold = "Zalo"
        val title = getString(R.string.confirm_otp, phone)
        val spanner = SpannableStringBuilder(title)
        spanner.setSpan(
            StyleSpan(Typeface.BOLD),
            title.lastIndexOf(textBold), title.lastIndexOf(textBold) + textBold.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanner.setSpan(
            StyleSpan(Typeface.BOLD),
            title.lastIndexOf(phone), title.lastIndexOf(phone) + phone.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvTitle.text = spanner
    }

    override fun setBackPress() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialog()
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), onBackPressedCallback)
    }

    private fun startCountdown() {
        countdownJob = CoroutineScope(Dispatchers.Main).launch {
            binding.tvNewOtp.setTextColor(requireActivity().getColor(R.color.grey))
            val defaultText = binding.tvNewOtp.text
            for (i in RESEND_EMAIL_TIME downTo 0) {
                if (i < 10)
                    binding.tvNewOtp.text = "$defaultText 00:0$i"
                else
                    binding.tvNewOtp.text = "$defaultText 00:$i"
                delay(1000)
            }
            binding.tvNewOtp.text = getString(R.string.new_otp)
            binding.tvNewOtp.setTextColor(requireActivity().getColor(R.color.green))
            binding.tvNewOtp.isEnabled = true
            countdownJob = null
        }
    }

    private fun showDialog() {
        ExitConfirmDialog.with(requireActivity()).setListener(
            object : ExitConfirmDialog.ExitDialogListener {
                override fun onClickYes() {
                    viewModel.replaceFragment(LoginPageAdapter.FRAGMENT_SIGN_IN)
                    destroy()
                }

                override fun onClickCancel() {
                }

            }
        ).show()
    }

    override fun setUpClick() {
        binding.tvNewOtp.setOnClickListener {
            startCountdown()
            it.isEnabled = false
        }
    }

    private fun observerData() {
        lifecycleScope.launch {
            viewModel.phoneNumber.collect {
                setUpUi()
            }
        }

        binding.textInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val textLength = p0?.length
                if (textLength == 6) {
                    if (viewModel.getIsLogin()) {
                        startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        viewModel.replaceFragment(LoginPageAdapter.FRAGMENT_ENTER_NAME)
                    }
                }
            }
        })
    }

    private fun setUpToolBar() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_back)
                setDisplayShowHomeEnabled(false)
                title = ""
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun handleInset() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomInset = maxOf(systemBarInsets.bottom, imeInsets.bottom)
            binding.root.setPadding(
                0,
                0,
                0,
                bottomInset
            )
            binding.toolbar.setPadding(0, systemBarInsets.top, 0, 0)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun destroy() {
        if (countdownJob?.isActive == true) {
            countdownJob?.cancel()
            countdownJob = null
        }
        binding.textInput.setText("")
        binding.tvNewOtp.text = getString(R.string.new_otp)
        binding.tvNewOtp.setTextColor(requireActivity().getColor(R.color.green))
    }

    companion object {
        const val RESEND_EMAIL_TIME = 60
    }
}