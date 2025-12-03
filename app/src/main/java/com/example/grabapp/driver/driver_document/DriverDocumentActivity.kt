package com.example.grabapp.driver.driver_document

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDriverDocumentBinding

class DriverDocumentActivity :
    BaseActivity<ActivityDriverDocumentBinding, DriverDocumentViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDriverDocumentBinding> =
        lazy { ActivityDriverDocumentBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<DriverDocumentViewModel> =
        lazy { DriverDocumentViewModel(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}
