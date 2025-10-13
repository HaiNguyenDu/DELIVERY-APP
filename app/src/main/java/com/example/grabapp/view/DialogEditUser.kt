package com.example.grabapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.grabapp.R
import com.example.grabapp.databinding.DialogBottomEditUserBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class DialogEditUser(private val context: Context) {
    private val binding = DialogBottomEditUserBinding.inflate(LayoutInflater.from(context))
    private val editUserDialog = BottomSheetDialog(context)
    private var dialogEditUserListener: DialogEditUserListener? = null
    init {
        editUserDialog.setContentView(binding.root)
        handleLayout()
        observerView()
    }
    fun observerView(){
        binding.btnExit.setOnClickListener {
            editUserDialog.dismiss()
        }
        binding.btnLogOut.setOnClickListener {
            dialogEditUserListener?.onLogOut()
        }
        binding.btnSave.setOnClickListener {
            dialogEditUserListener?.onSave()
        }
    }
    fun handleLayout(){
        editUserDialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
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
    fun setListener(dialogEditUserListener: DialogEditUserListener): DialogEditUser
    {
        this.dialogEditUserListener = dialogEditUserListener
        return this
    }

    fun show()
    {
        editUserDialog.show()
    }
}

interface DialogEditUserListener {
    fun onSave()
    fun onLogOut()
}