package com.example.grabapp.ui.login.fragment

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentEnterPasswordBinding
import com.example.grabapp.extention.hideKeyboard
import com.example.grabapp.ui.home.MainActivity
import com.example.grabapp.ui.login.LoginViewModel
import com.example.grabapp.ui.login.adapter.LoginPageAdapter
import com.example.grabapp.view.ExitConfirmDialog
import com.example.grabapp.view.SnackBarCustom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EnterPasswordFragment : BaseFragment<FragmentEnterPasswordBinding, LoginViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentEnterPasswordBinding> =
        lazy { FragmentEnterPasswordBinding.inflate(layoutInflater) }

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
        observerView()
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
                viewModel.setPassword(p0.toString())
            }

        })
    }

    private fun observerView(){
        lifecycleScope.launch {
            viewModel.password.collect {
                val length = it.length
                if (length > 0) {
                    binding.btnNext.alpha = 1f
                    binding.btnNext.isEnabled = true
                } else {
                    binding.btnNext.alpha = 0.6f
                    binding.btnNext.isEnabled = false
                }
            }
        }
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
                val password = binding.textInput.text
                if (password == null || password.length <= 6) {
                    SnackBarCustom(
                        view = binding.root,
                        message = getString(R.string.enter_password_fail),
                        backgroundColor = context?.getColor(R.color.white)!!,
                        textColor = context?.getColor(R.color.green)!!,
                        bottomMarginDp = 100f,
                    ).show()
                } else {
                    if (viewModel.getIsLogin()) {
                        viewModel.login({
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                        }) {
                            SnackBarCustom(
                                view = binding.root,
                                message = getString(R.string.enter_password_fail),
                                backgroundColor = context?.getColor(R.color.white)!!,
                                textColor = context?.getColor(R.color.green)!!,
                                bottomMarginDp = 100f,
                            ).show()
                        }
                    } else
                        viewModel.replaceFragment(LoginPageAdapter.FRAGMENT_ENTER_NAME)
                }
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