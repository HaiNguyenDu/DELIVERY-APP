package com.example.grabapp.ui.home

import android.os.Bundle
import android.view.View
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

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