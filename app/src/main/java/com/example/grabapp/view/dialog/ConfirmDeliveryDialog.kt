package com.example.grabapp.view.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.grabapp.base.BaseDialogFragment
import com.example.grabapp.databinding.DialogConfirmDeliveryBinding
import com.example.grabapp.databinding.DialogNewOrderedBinding

class ConfirmDeliveryDialog : BaseDialogFragment<DialogConfirmDeliveryBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogConfirmDeliveryBinding {
        return DialogConfirmDeliveryBinding.inflate(inflater, container, false)
    }

    override fun setUpInit() = Unit
}