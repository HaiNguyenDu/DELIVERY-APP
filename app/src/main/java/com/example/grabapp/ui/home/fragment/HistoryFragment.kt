package com.example.grabapp.ui.home.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentHistoryBinding
import com.example.grabapp.ui.home.MainViewModel

class HistoryFragment: BaseFragment<FragmentHistoryBinding, MainViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentHistoryBinding> = lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    override fun getLazyViewModel(): Lazy<MainViewModel> = lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun setUpClick() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}