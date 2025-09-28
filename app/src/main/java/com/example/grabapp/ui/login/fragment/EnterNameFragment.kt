package com.example.grabapp.ui.login.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentEnterNameBinding
import com.example.grabapp.extention.hideKeyboard
import com.example.grabapp.ui.login.LoginViewModel
import com.example.grabapp.ui.login.adapter.LoginPageAdapter
import com.example.grabapp.view.ExitConfirmDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EnterNameFragment : BaseFragment<FragmentEnterNameBinding, LoginViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentEnterNameBinding> =
        lazy { FragmentEnterNameBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<LoginViewModel> =
        lazy { ViewModelProvider(requireActivity())[LoginViewModel::class.java] }

    private lateinit var onBackPressedCallback: OnBackPressedCallback
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

    private fun setUpUi() {
        val spanner = SpannableStringBuilder(getString(R.string.policy))
        val mainText = getString(R.string.policy)
        val text1 = "Điều Khoản Dịch Vụ"
        val indexText1 = mainText.lastIndexOf(text1)
        val text2 = "Thông Báo Bảo Mật"
        val indexText2 = mainText.lastIndexOf(text2)
        spanner.setSpan(
            ForegroundColorSpan(requireActivity().getColor(R.color.green)),
            indexText1,
            indexText1 + text1.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanner.setSpan(
            StyleSpan(Typeface.BOLD),
            indexText1,
            indexText1 + text1.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanner.setSpan(
            ForegroundColorSpan(requireActivity().getColor(R.color.green)),
            indexText2,
            indexText2 + text2.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanner.setSpan(
            StyleSpan(Typeface.BOLD),
            indexText2,
            indexText2 + text2.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvPolicy.text = spanner
        binding.btnNext.alpha = 0.6f
        binding.btnNext.isEnabled = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleInset()
        setUpToolBar()
        setUpUi()
        observerData()
    }

    private fun observerData() {
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
                val length = p0?.length ?: 0
                if (length > 0) {
                    binding.btnNext.alpha = 1f
                    binding.btnNext.isEnabled = true
                } else {
                    binding.btnNext.alpha = 0.6f
                    binding.btnNext.isEnabled = false
                }


            }

        })
    }

    override fun setBackPress() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialogExit()
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), onBackPressedCallback)
    }

    private fun setUpToolBar() {
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_back)
                setDisplayShowHomeEnabled(false)
                title = getString(R.string.start)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    override fun setUpClick() {
        binding.btnNext.setOnClickListener {
            hideKeyboard()
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.showLoading()
                delay(5000)
                viewModel.hideLoading()
            }
        }
    }

    private fun showDialogExit() {
        ExitConfirmDialog.with(requireActivity()).setListener(
            object : ExitConfirmDialog.ExitDialogListener {
                override fun onClickYes() {
                    viewModel.replaceFragment(LoginPageAdapter.FRAGMENT_LOGIN)
                    viewModel.setPhoneNumber("")
                    binding.textInput.setText("")
                }

                override fun onClickCancel() {
                }
            }
        ).show()
    }
}