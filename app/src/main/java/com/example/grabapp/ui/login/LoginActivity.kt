package com.example.grabapp.ui.login

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.graphics.Insets
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.ActivityLoginBinding
import com.example.grabapp.ui.login.adapter.LoginPageAdapter
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityLoginBinding> =
        lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<LoginViewModel> =
        lazy { ViewModelProvider(this)[LoginViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setUpUi()
        observerData()
    }

    override fun handleInsets(v: View, insets: Insets) {}

    private fun setUpUi() {
        binding.viewPage2.apply {
            adapter = LoginPageAdapter(this@LoginActivity)
            isUserInputEnabled = false
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val fragment = supportFragmentManager.findFragmentByTag("f$position")
                    if (fragment is BaseFragment<*, *>)
                        fragment.setBackPress()

                }
            })
        }
    }

    private fun observerData() {
        lifecycleScope.launch {
            viewModel.currentFragmentIndex.collect { index ->
                binding.viewPage2.post {
                    binding.viewPage2.setCurrentItem(index, true)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding.lottie.isVisible = it
            }
        }
    }
}