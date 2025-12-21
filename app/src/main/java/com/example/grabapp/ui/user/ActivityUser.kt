package com.example.grabapp.ui.user

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.Insets
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.R
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityUserBinding
import com.example.grabapp.utils.SessionManager
import com.example.grabapp.view.DialogEditUser
import kotlinx.coroutines.launch

class ActivityUser : BaseActivity<ActivityUserBinding, UserViewModel>() {
    private var dialogEditUser: DialogEditUser? = null
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                viewModel.setImageUrl(uri.toString())
                dialogEditUser?.setImageUrl(uri.toString())
            }
        }

    override fun getLazyBinding(): Lazy<ActivityUserBinding> = lazy {
        ActivityUserBinding.inflate(layoutInflater)
    }

    override fun getLazyViewModel(): Lazy<UserViewModel> = lazy {
        UserViewModel(application)
    }

    override fun handleInsets(v: View, insets: Insets) {
        super.handleInsets(v, insets)
        v.setPadding(insets.left, 0, insets.right, insets.bottom)
        binding.layoutToolbar.setPadding(0, insets.top, 0, binding.layoutToolbar.paddingBottom)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeView()
        observerData()
    }

    private fun observerData() {
        lifecycleScope.launch {
            viewModel.user.collect {
                it?.let {
                    binding.tvUsername.text = it.fullName
                }
            }
        }
        lifecycleScope.launch {
            viewModel.isUpdate.collect {
                if (it) {
                    dialogEditUser?.dismiss()
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.anim_translate_in_right,
            R.anim.anim_translate_out_left
        )
    }

    private fun observeView() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            btnUserDetail.setOnClickListener {
                viewModel.getUser()?.let {
                    if (dialogEditUser == null) {
                        dialogEditUser =
                            DialogEditUser(this@ActivityUser).setUser(it)
                                .setListener(
                                    object : DialogEditUser.DialogEditUserListener {
                                        override fun onSave(
                                            phone: String,
                                            fullName: String,
                                            date: String
                                        ) {
                                            viewModel.updateUser(
                                                this@ActivityUser,
                                                phone,
                                                fullName,
                                                date
                                            )
                                        }

                                        override fun onPickImage() {
                                            pickImageLauncher.launch("image/*")
                                        }

                                        override fun onLogOut() {
                                            SessionManager.triggerLogout()
                                        }
                                    }
                                )
                        dialogEditUser?.show()
                    } else dialogEditUser?.setUser(it)?.show()
                }
            }
        }
    }
}