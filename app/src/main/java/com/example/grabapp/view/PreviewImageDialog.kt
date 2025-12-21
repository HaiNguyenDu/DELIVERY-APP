package com.example.grabapp.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.example.grabapp.databinding.DialogPreviewImageBinding

class PreviewImageDialog(val context: Context, url: String) {
    private val binding = DialogPreviewImageBinding.inflate(LayoutInflater.from(context))
    private val builder = AlertDialog.Builder(context).setView(binding.root)
    private lateinit var previewDialog: AlertDialog

    init {
        Glide.with(context).load(url).into(binding.iv)
    }

    fun show() {
        previewDialog = builder.create().apply {
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

    companion object {
        fun with(context: Context, url: String): PreviewImageDialog {
            return PreviewImageDialog(context, url)
        }
    }
}