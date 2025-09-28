package com.example.grabapp.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.example.grabapp.databinding.DialogExitConfirmBinding
import androidx.core.graphics.drawable.toDrawable

class ExitConfirmDialog(context: Context) {
    private val binding = DialogExitConfirmBinding.inflate(LayoutInflater.from(context))
    private val builder = AlertDialog.Builder(context).setView(binding.root)
    private lateinit var exitDialog: AlertDialog
    private lateinit var listener: ExitDialogListener

    init {
        binding.btnYes.setOnClickListener {
            if (::listener.isInitialized) {
                listener.onClickYes()
                exitDialog.dismiss()
            }
        }
        binding.btnNo.setOnClickListener {
            if (::listener.isInitialized) {
                listener.onClickCancel()
                exitDialog.dismiss()
            }
        }
    }

    fun setTitle(title: String): ExitConfirmDialog {
        binding.tvTitle.text = title
        return this
    }

    fun setMessage(message: String): ExitConfirmDialog {
        binding.tvConfirm.text = message
        return this
    }

    fun setListener(listener: ExitDialogListener): ExitConfirmDialog {
        this.listener = listener
        return this
    }

    fun show() {
        exitDialog = builder.create().apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            show()
            window?.apply {
                setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            }
        }
    }

    interface ExitDialogListener {
        fun onClickYes()
        fun onClickCancel()
    }

    companion object {
        fun with(context: Context): ExitConfirmDialog {
            return ExitConfirmDialog(context)
        }
    }
}