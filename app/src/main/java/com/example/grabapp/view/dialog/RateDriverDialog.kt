package com.example.grabapp.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.databinding.DialogRateDriverBinding

class RateDriverDialog : BaseDialogFragment<DialogRateDriverBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogRateDriverBinding {
        return DialogRateDriverBinding.inflate(inflater, container, false)
    }

    override fun setUpInit() = Unit
}
