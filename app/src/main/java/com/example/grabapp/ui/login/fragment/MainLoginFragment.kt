package com.example.grabapp.ui.login.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentMainLoginBinding
import com.example.grabapp.ui.login.LoginViewModel
import com.example.grabapp.ui.login.adapter.LoginPageAdapter

class MainLoginFragment : BaseFragment<FragmentMainLoginBinding, LoginViewModel>() {

    override fun getLazyBinding(): Lazy<FragmentMainLoginBinding> =
        lazy { FragmentMainLoginBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<LoginViewModel> =
        lazy { ViewModelProvider(requireActivity())[LoginViewModel::class.java] }

    private fun handleInset(){
        ViewCompat.setOnApplyWindowInsetsListener(binding.root){v, insets ->
            val padding = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.setPadding(0,0,0,padding.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleInset()
        setUpClick()
    }


    private fun setUpClick(){
        binding.btnSigIn.setOnClickListener {
            viewModel.replaceFragment(LoginPageAdapter.FRAGMENT_SIGN_IN)
        }

        binding.btnSignUp.setOnClickListener {
            viewModel.replaceFragment(LoginPageAdapter.FRAGMENT_SIGN_IN)
        }
    }
}