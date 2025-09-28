package com.example.grabapp.ui.login.fragment

import android.os.Bundle
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
    }

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun setBackPress(){
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

    private fun setUpClick() {
        binding.btnNext.setOnClickListener {
            requireActivity().lifecycleScope.launch {
                viewModel.showLoading()
                delay(1000)
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
                    viewModel.replaceFragment(LoginPageAdapter.FRAGMENT_CONFIRM)
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