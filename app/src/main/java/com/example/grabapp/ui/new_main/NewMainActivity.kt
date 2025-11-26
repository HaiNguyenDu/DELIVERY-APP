package com.example.grabapp.ui.new_main

import android.os.Bundle
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityNewMainBinding
import com.example.grabapp.ui.new_main.adapter.MainPageAdapter
import com.example.grabapp.ui.new_main.adapter.MainPageAdapter.Companion.FRAGMENT_ADD_IMAGE
import com.example.grabapp.ui.new_main.adapter.MainPageAdapter.Companion.FRAGMENT_MAIN
import com.google.android.material.tabs.TabLayoutMediator

class NewMainActivity : BaseActivity<ActivityNewMainBinding, NewMainViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityNewMainBinding> = lazy {
        ActivityNewMainBinding.inflate(layoutInflater)
    }

    override fun getLazyViewModel(): Lazy<NewMainViewModel> = lazy {
        NewMainViewModel(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.viewPage.adapter = MainPageAdapter(this)
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPage
        ) { tab, position ->
            when(position){
                FRAGMENT_MAIN -> {
                    tab.text = "Main"
                    tab.icon = getDrawable(R.drawable.ic_home)
                }
                FRAGMENT_ADD_IMAGE -> {
                    tab.text = "Add Image"
                    tab.icon = getDrawable(R.drawable.ic_map)
                }
            }
        }.attach()
    }
}