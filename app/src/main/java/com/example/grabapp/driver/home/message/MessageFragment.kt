package com.example.grabapp.driver.home.message

import android.view.View
import androidx.core.graphics.Insets
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.FileRepository
import com.example.grabapp.databinding.FragmentMessageBinding
import com.example.grabapp.driver.home.DriverHomeViewModel

class MessageFragment : BaseFragment<FragmentMessageBinding, DriverHomeViewModel>() {
    override fun getLazyBinding(): Lazy<FragmentMessageBinding> =
        lazy { FragmentMessageBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverHomeViewModel> =
        lazy {
            val fileRepository = FileRepository()
            val aiServiceRepository = AIServiceRepository()
            DriverHomeViewModel(requireActivity().application, fileRepository, aiServiceRepository)
        }

    override fun setUpClick() = Unit

    override fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        binding.emptyView.setPadding(0, inset.top, 0, 0)
        binding.ctlTopBar.setPadding(0, inset.top / 2, 0, 0)
    }
}
