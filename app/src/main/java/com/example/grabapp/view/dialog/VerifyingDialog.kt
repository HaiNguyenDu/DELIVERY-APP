package com.example.grabapp.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.databinding.DialogVerifyingBinding

class VerifyingDialog : BaseDialogFragment<DialogVerifyingBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogVerifyingBinding {
        return DialogVerifyingBinding.inflate(inflater, container, false)
    }

    override fun setUpInit() = Unit
}
