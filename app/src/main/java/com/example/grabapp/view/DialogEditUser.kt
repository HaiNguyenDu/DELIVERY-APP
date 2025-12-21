package com.example.grabapp.view

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.grabapp.R
import com.example.grabapp.databinding.DialogBottomEditUserBinding
import com.example.grabapp.domain.model.user.User
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DialogEditUser(private val context: Context) {

    private val binding =
        DialogBottomEditUserBinding.inflate(LayoutInflater.from(context))

    private val dialog = BottomSheetDialog(context)
    private var listener: DialogEditUserListener? = null
    private var user: User? = null

    init {
        dialog.setContentView(binding.root)
        handleLayout()
        setupView()
    }

    private fun setupView() {

        binding.btnExit.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnLogOut.setOnClickListener {
            listener?.onLogOut()
        }

        binding.btnSave.setOnClickListener {
            listener?.onSave(
                binding.edtPhone.text.toString(),
                binding.edtUsername.text.toString(),
                binding.edtDob.text.toString()
            )
        }

        binding.edtDob.setOnClickListener {
            showDatePicker()
        }

        binding.ivAvatar.setOnClickListener {
            listener?.onPickImage()
        }
    }

    fun setUser(user: User): DialogEditUser {
        this.user = user
        binding.edtUsername.setText(user.fullName)
        binding.edtPhone.setText(user.phone)
        binding.edtDob.setText(user.date)

        Glide.with(context)
            .load(user.avatarUrl)
            .placeholder(R.drawable.ic_avatar_default)
            .error(R.drawable.ic_avatar_default)
            .circleCrop()
            .into(binding.ivAvatar)
        return this
    }

    fun setImageUrl(url: String) {
        Glide.with(context)
            .load(url.toUri())
            .placeholder(R.drawable.ic_avatar_default)
            .error(R.drawable.ic_avatar_default)
            .circleCrop()
            .into(binding.ivAvatar)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val formatter =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.edtDob.setText(formatter.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }

    private fun handleLayout() {
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
            bottomSheet?.let {
                BottomSheetBehavior.from(it).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                }
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.setBackgroundColor(context.getColor(R.color.white))
            }
        }
    }

    fun setListener(listener: DialogEditUserListener): DialogEditUser {
        this.listener = listener
        return this
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    interface DialogEditUserListener {
        fun onSave(phone: String, fullName: String, date: String)
        fun onPickImage()
        fun onLogOut()
    }

}