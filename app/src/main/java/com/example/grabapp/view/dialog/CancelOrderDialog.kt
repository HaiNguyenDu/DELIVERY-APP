package com.example.grabapp.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.databinding.DialogCancelOrderBinding

class CancelOrderDialog : BaseDialogFragment<DialogCancelOrderBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogCancelOrderBinding {
        return DialogCancelOrderBinding.inflate(inflater, container, false)
    }

    override fun setUpInit() = Unit
}
