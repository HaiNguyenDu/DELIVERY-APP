package com.example.grabapp.view.dialog

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.databinding.DialogVerifyingBinding
import com.example.grabapp.model.VerifyingState

class VerifyingDialog : BaseDialogFragment<DialogVerifyingBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogVerifyingBinding {
        return DialogVerifyingBinding.inflate(inflater, container, false)
    }

    override fun width() = 0.5f

    override fun setUpInit() {
        setupLottieAnimations()
        setState(VerifyingState.Verifying)
    }

    private fun setupLottieAnimations() {
        binding.lottieVerifySuccess.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                dismiss()
            }
        })
    }

    fun setState(state: VerifyingState) {
        if (!isAdded || view == null) {
            return
        }
        
        when (state) {
            VerifyingState.Verifying -> {
                binding.lottieVerifying.isVisible = true
                binding.lottieVerifySuccess.isVisible = false
                binding.lottieVerifying.playAnimation()
            }
            VerifyingState.Success -> {
                binding.lottieVerifying.isVisible = false
                binding.lottieVerifySuccess.isVisible = true
                binding.lottieVerifySuccess.apply {
                    repeatCount = 0
                    playAnimation()
                }
            }
        }
    }
}
