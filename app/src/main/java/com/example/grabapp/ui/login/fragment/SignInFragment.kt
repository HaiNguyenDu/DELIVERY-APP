package com.example.grabapp.ui.login.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentSignInBinding
import com.example.grabapp.extention.hideKeyboard
import com.example.grabapp.ui.login.LoginViewModel
import com.example.grabapp.ui.login.adapter.LoginPageAdapter
import com.example.grabapp.view.ExitConfirmDialog
import com.example.grabapp.view.SnackBarCustom
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignInFragment : BaseFragment<FragmentSignInBinding, LoginViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentSignInBinding> =
        lazy { FragmentSignInBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<LoginViewModel> =
        lazy { ViewModelProvider(requireActivity())[LoginViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        handleInset()
        setUpClick()
        observerData()
        setUpUi()
    }

    private fun setUpUi(){
        binding.btnNext.alpha = 0.6f
        binding.btnNext.isEnabled = false
    }
    private fun observerData(){
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
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun setBackPress() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDialog()
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(requireActivity(), onBackPressedCallback)
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

    override fun setUpClick() {
        binding.btnNext.setOnClickListener {
            hideKeyboard()
            requireActivity().lifecycleScope.launch {
                viewModel.showLoading()
                delay(3000)
                val phoneNumber = binding.textInput.text
                if (phoneNumber == null || phoneNumber.length < 9 || phoneNumber.length > 12) {
                    SnackBarCustom(
                        view = binding.root,
                        message = getString(R.string.entry_phone_request),
                        backgroundColor = context?.getColor(R.color.white)!!,
                        textColor = context?.getColor(R.color.green)!!,
                        bottomMarginDp = 100f,
                    ).show()
                } else {
                    viewModel.setPhoneNumber(phoneNumber.toString())
                    viewModel.replaceFragment(LoginPageAdapter.FRAGMENT_PASSWORD)
                }
                viewModel.hideLoading()
            }
        }
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

    private fun showDialog() {
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