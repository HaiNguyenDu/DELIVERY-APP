package com.example.grabapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityMainBinding> = lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<MainViewModel> {
        return lazy { MainViewModel(application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        observeView()
    }


    private fun observeView() {
        binding.edt.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->

            binding.layoutEdt.background =
                if (hasFocus) getDrawable(R.drawable.bg_edt_search_forcus) else getDrawable(R.drawable.bg_edit_search)
        }
    }

}