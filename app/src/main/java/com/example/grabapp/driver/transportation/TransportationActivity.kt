package com.example.grabapp.driver.transportation

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityTransportationBinding

class TransportationActivity :
    BaseActivity<ActivityTransportationBinding, TransportationViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityTransportationBinding> =
        lazy { ActivityTransportationBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<TransportationViewModel> =
        lazy { TransportationViewModel(application) }
}
