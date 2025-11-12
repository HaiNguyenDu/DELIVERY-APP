package com.example.grabapp.driver.order_detail

import com.example.grabapp.base.BaseActivity
import com.example.grabapp.databinding.ActivityDetailOrderBinding

class OrderDetailActivity : BaseActivity<ActivityDetailOrderBinding, OrderDetailViewModel>() {
    override fun getLazyBinding(): Lazy<ActivityDetailOrderBinding> =
        lazy { ActivityDetailOrderBinding.inflate(layoutInflater) }

    override fun getLazyViewModel(): Lazy<OrderDetailViewModel> =
        lazy { OrderDetailViewModel(application) }
}
