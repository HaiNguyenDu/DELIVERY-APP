package com.example.grabapp.driver.login

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.api.RetrofitProvider
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.AuthApi
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.data.repository.AuthRepository
import com.example.grabapp.databinding.ActivityDriverLoginBinding
import com.example.grabapp.driver.home.DriverHomeActivity
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity
import retrofit2.Retrofit

class DriverLoginActivity : BaseActivity<ActivityDriverLoginBinding, DriverLoginViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverLoginBinding> =
        lazy { ActivityDriverLoginBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverLoginViewModel> =
        lazy {
            val retrofit = RetrofitProvider.create("https://quickdn.undo.it/")
            val api = retrofit.create(AuthApi::class.java)
            val tokenStorage = TokenStorage(applicationContext)
            val repo = AuthRepository(api, tokenStorage)
            DriverLoginViewModel(application, repo)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListener()
        observeViewModel()
    }

    private fun setupListener() {
        binding.apply {
            flSignIn.onClickWithScale {
                val phone = binding.edtPhoneNumber.text?.toString()?.trim() ?: ""
                val password = binding.edtPassword.text?.toString().orEmpty()
                viewModel.login(phone, password)
            }
            ivLoginByFace.onClickWithScale {

            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.errorMessage.collect { err ->
                err?.let {
                    Toast.makeText(this@DriverLoginActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.loading.collect { loading ->
                binding.flSignIn.isEnabled = !loading
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { success ->
                if (success) {
                    startActivity<DriverHomeActivity>()
                    finish()
                }
            }
        }
    }
}
