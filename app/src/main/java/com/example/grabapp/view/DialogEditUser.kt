package com.example.grabapp.view

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grabapp.R
import com.example.grabapp.databinding.DialogBottomEditUserBinding
import com.example.grabapp.domain.model.user.User
import com.example.grabapp.extention.formatToVietNamTime
import com.example.grabapp.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DialogEditUser(private val context: Context) {
    private val binding = DialogBottomEditUserBinding.inflate(LayoutInflater.from(context))
    private val editUserDialog = BottomSheetDialog(context)
    private var dialogEditUserListener: DialogEditUserListener? = null

    private var user: User? = null

    init {
        editUserDialog.setContentView(binding.root)
        handleLayout()
        observerView()
    }

    fun observerView() {
        binding.btnExit.setOnClickListener {
            editUserDialog.dismiss()
        }
        binding.btnLogOut.setOnClickListener {
            dialogEditUserListener?.onLogOut()
        }
        binding.btnSave.setOnClickListener {
            dialogEditUserListener?.onSave()
        }
        binding.edtDob.setOnClickListener {
            setupDatePicker()
        }
    }

    fun setUser(user: User): DialogEditUser {
        this.user = user
        binding.edtUsername.setText(user.fullName)
        binding.edtPhone.setText(user.phone)
        binding.edtDob.setText(user.date )
        return this
    }
    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()

        binding.edtDob.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    calendar.set(selectedYear, selectedMonth, selectedDay)
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    binding.edtDob.setText(formatter.format(calendar.time))
                },
                year,
                month,
                day
            )
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }
    }
    fun handleLayout() {
        editUserDialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.setBackgroundColor(context.getColor(R.color.white))
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                0
            )

            WindowInsetsCompat.CONSUMED
        }
    }

    fun setListener(dialogEditUserListener: DialogEditUserListener): DialogEditUser {
        this.dialogEditUserListener = dialogEditUserListener
        return this
    }

    fun show() {
        editUserDialog.show()
    }
}

interface DialogEditUserListener {
    fun onSave()
    fun onLogOut()
}