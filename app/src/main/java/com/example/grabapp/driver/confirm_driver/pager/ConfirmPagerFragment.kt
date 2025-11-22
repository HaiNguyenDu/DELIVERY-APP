package com.example.grabapp.driver.confirm_driver.pager

import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.databinding.FragmentConfirmPagerBinding
import com.example.grabapp.driver.confirm_driver.ConfirmDriverViewModel
import com.example.grabapp.extention.onClickWithScale
import kotlinx.coroutines.launch

class ConfirmPagerFragment : BaseFragment<FragmentConfirmPagerBinding, ConfirmDriverViewModel>() {

    private var currentPager: ConfirmPager? = null

    override fun getLazyBinding(): Lazy<FragmentConfirmPagerBinding> =
        lazy { FragmentConfirmPagerBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<ConfirmDriverViewModel> = lazy {
        ViewModelProvider(requireActivity())[ConfirmDriverViewModel::class.java]
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(ConfirmPagerAdapter.POSITION, 0) ?: 0
        currentPager = ConfirmPager.entries.getOrNull(position)
        setupView()
        observeViewModel()
    }

    private fun setupView() {
        currentPager?.let { pager ->
            binding.tvStep.setText(pager.pageStepRes)
            binding.tvTitle.setText(pager.pageTitleRes)
            binding.tvContent.setText(pager.pageContentRes)
        }
        updateButtonStates()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.pagerStates.collect { states ->
                updateButtonStates()
            }
        }
    }

    private fun updateButtonStates() {
        currentPager?.let { pager ->
            val status = viewModel.pagerStates.value[pager] ?: ConfirmationStatus.NONE

            // Update tvNo
            if (status == ConfirmationStatus.NO) {
                binding.tvNo.apply {
                    background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_gradient_green_100
                    )
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            } else {
                binding.tvNo.apply {
                    background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_border_green_100_10_per
                    )
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.green_20))
                }
            }

            // Update tvYes
            if (status == ConfirmationStatus.YES) {
                binding.tvYes.apply {
                    background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_gradient_green_100
                    )
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                }
            } else {
                binding.tvYes.apply {
                    background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.bg_border_green_100_10_per
                    )
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.green_20))
                }
            }
        }
    }

    override fun setUpClick() {
        binding.tvNo.onClickWithScale {
            currentPager?.let { pager ->
                viewModel.updateConfirmationStatus(pager, ConfirmationStatus.NO)
                handleConfirmationClick(pager)
            }
        }

        binding.tvYes.onClickWithScale {
            currentPager?.let { pager ->
                viewModel.updateConfirmationStatus(pager, ConfirmationStatus.YES)
                handleConfirmationClick(pager)
            }
        }
    }

    private fun handleConfirmationClick(pager: ConfirmPager) {
        if (pager == ConfirmPager.CONFIRM_PAGE_04) {
            if (viewModel.areAllPagesConfirmed()) {
                viewModel.navigateRegister()
            } else {
                viewModel.navigateEligibility()
            }
        } else {
            viewModel.navigateNextPage()
        }
    }
}
