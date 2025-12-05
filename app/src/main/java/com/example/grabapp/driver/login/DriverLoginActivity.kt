package com.example.grabapp.driver.login

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.grabapp.api.RetrofitProvider
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.data.TokenStorage
import com.example.grabapp.data.auth.AuthApi
import com.example.grabapp.data.repository.AIServiceRepository
import com.example.grabapp.data.repository.AddressRepository
import com.example.grabapp.data.repository.AuthRepository
import com.example.grabapp.databinding.ActivityDriverLoginBinding
import com.example.grabapp.driver.home.DriverHomeActivity
import com.example.grabapp.model.VerifyingState
import com.example.grabapp.extention.onClickWithScale
import com.example.grabapp.extention.startActivity
import com.example.grabapp.view.dialog.FaceCaptureDialog
import com.example.grabapp.view.dialog.VerifyingDialog
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import java.io.File

class DriverLoginActivity : BaseActivity<ActivityDriverLoginBinding, DriverLoginViewModel>() {
    private var verifyingDialog: VerifyingDialog? = null
    private var faceCaptureDialog: FaceCaptureDialog? = null
    private val tokenStorage by lazy { TokenStorage(applicationContext) }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openFaceCaptureDialog()
            } else {
                Toast.makeText(
                    this,
                    "Cần cấp quyền camera để đăng nhập bằng khuôn mặt",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun getLazyBinding(): Lazy<ActivityDriverLoginBinding> =
        lazy { ActivityDriverLoginBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverLoginViewModel> =
        lazy {
            val retrofit = RetrofitProvider.create("https://quickdn.undo.it/")
            val api = retrofit.create(AuthApi::class.java)
            val tokenStorage = TokenStorage(applicationContext)
            val authRepo = AuthRepository(api, tokenStorage)
            val aiServiceRepo = AIServiceRepository()
            DriverLoginViewModel(application, authRepo, aiServiceRepo, tokenStorage)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListener()
        observeViewModel()
        MapLibre.getInstance(
            this,
            AddressRepository.API_KEY,
            WellKnownTileServer.MapLibre
        )
    }

    private fun setupListener() {
        binding.apply {
            flSignIn.onClickWithScale {
                val phone = binding.edtPhoneNumber.text?.toString()?.trim() ?: ""
                val password = binding.edtPassword.text?.toString().orEmpty()
                viewModel.login(phone, password)
            }
            ivLoginByFace.onClickWithScale {
                checkCameraPermissionAndOpen()
            }
        }
    }

    private fun checkCameraPermissionAndOpen() {
        val userId = tokenStorage.getUserId()
        if (userId == null) {
            Toast.makeText(
                this,
                "Vui lòng đăng nhập lần đầu bằng số điện thoại và mật khẩu",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openFaceCaptureDialog()
            }

            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openFaceCaptureDialog() {
        val userId = tokenStorage.getUserId()
        if (userId == null) {
            Toast.makeText(
                this,
                "Vui lòng đăng nhập lần đầu bằng số điện thoại và mật khẩu",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        faceCaptureDialog = FaceCaptureDialog().apply {
            isCancelable = false
            setOnFaceCapturedListener { bitmap ->
                showVerifyingDialog()
                viewModel.verifyFace(bitmap, userId)
            }
            setOnDismissListener {
                faceCaptureDialog = null
            }
        }
        faceCaptureDialog?.showDialog(supportFragmentManager)
    }

    private fun showVerifyingDialog() {
        verifyingDialog = VerifyingDialog().apply {
            isCancelable = false
        }
        verifyingDialog?.show(supportFragmentManager, "VerifyingDialog")
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.errorMessage.collect { err ->
                err?.let {
                    Toast.makeText(this@DriverLoginActivity, it, Toast.LENGTH_SHORT).show()
                    Log.e("DriverLoginActivity", it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.loading.collect { loading ->
                binding.flSignIn.isEnabled = !loading
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginEvent.collect { success ->
                if (success) {
                    verifyingDialog?.dismiss()
                    startActivity<DriverHomeActivity>()
                    finish()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.faceVerifyState.collect { state ->
                when (state) {
                    is FaceVerifyState.Idle -> {
                    }

                    is FaceVerifyState.Verifying -> {
                        verifyingDialog?.view?.post {
                            verifyingDialog?.setState(VerifyingState.Verifying)
                        } ?: run {
                            window.decorView.postDelayed({
                                verifyingDialog?.setState(VerifyingState.Verifying)
                            }, 200)
                        }
                    }

                    is FaceVerifyState.Success -> {
                        verifyingDialog?.view?.post {
                            verifyingDialog?.setState(VerifyingState.Success)
                        }
                    }

                    is FaceVerifyState.Error -> {
                        verifyingDialog?.dismiss()
                        verifyingDialog = null
                        Toast.makeText(this@DriverLoginActivity, state.message, Toast.LENGTH_LONG)
                            .show()
                        Log.e("DriverLoginActivity", state.message)
                    }
                }
            }
        }
    }
}

