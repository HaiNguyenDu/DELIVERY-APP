package com.example.grabapp.ui.home.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grabapp.R
import com.example.grabapp.base.BaseFragment
import com.example.grabapp.data.local.AppDatabase
import com.example.grabapp.databinding.FragmentMainBinding
import com.example.grabapp.ui.address_selection.AddressSelectionActivity
import com.example.grabapp.ui.home.MainViewModel
import com.example.grabapp.ui.home.MainViewModelFactory
import com.example.grabapp.ui.home.adapter.ADSAdapter
import com.example.grabapp.ui.user.ActivityUser
import com.facebook.shimmer.Shimmer
import kotlinx.coroutines.launch

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {
    val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        val fineLocationPermission = permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationPermission =
            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationPermission && coarseLocationPermission) {
            startActivity(Intent(requireContext(), AddressSelectionActivity::class.java))
            requireActivity().overridePendingTransition(
                R.anim.anim_translate_in_right,
                R.anim.anim_translate_out_left
            )
        }
    }

    override fun getLazyBinding(): Lazy<FragmentMainBinding> = lazy {
        FragmentMainBinding.inflate(layoutInflater)
    }

    override fun getLazyViewModel(): Lazy<MainViewModel> =
        lazy {
            val userDao = AppDatabase.getInstance(requireContext()).userDao()
            val factory = MainViewModelFactory(userDao, requireActivity().application)
            ViewModelProvider(requireActivity(),factory)[MainViewModel::class.java]
        }


    override fun setUpClick() {

    }

    private fun observerData() {
        viewLifecycleOwner.lifecycle.coroutineScope.launch {
            viewModel.user.collect {
                if (it != null)
                    binding.tvUsername.text = it.fullName
            }
        }
    }

    private fun initView() {
        binding.rcvQc.adapter = ADSAdapter()
        binding.rcvQc.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcvQc2.adapter = ADSAdapter()
        binding.rcvQc2.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shimmer()
        observeView()
        initView()
        observerData()
    }

    fun shimmer() {
        val shimmerBuilder = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(1f)
            .setHighlightAlpha(0.08f)
            .setTilt(30f)
            .setDropoff(0.5f)
            .setDuration(1500L)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setRepeatCount(0)
            .setShape(Shimmer.Shape.LINEAR)

        binding.shimmer.setShimmer(shimmerBuilder.build())
        val handler = Handler(Looper.getMainLooper())
        val shimmerRunnable = object : Runnable {
            override fun run() {
                binding.shimmer.setShimmer(shimmerBuilder.build())
                binding.shimmer.startShimmer()
                handler.postDelayed({
                    binding.shimmer.stopShimmer()
                }, 1200L)
                handler.postDelayed(this, 6000L)
            }
        }

        handler.postDelayed(shimmerRunnable, 2000L)
    }

    override fun handleInset(v: View, insets: Insets, bottomInset: Int) {
        v.setPadding(insets.left, 0, insets.right, 0)
        binding.layoutHeader.setPadding(
            binding.layoutHeader.paddingLeft,
            insets.top,
            binding.layoutHeader.paddingEnd,
            0
        )
    }

    private fun observeView() {
        binding.btnUser.setOnClickListener {
            startActivity(Intent(requireActivity(), ActivityUser::class.java))
            requireActivity().overridePendingTransition(
                R.anim.anim_translate_in_right,
                R.anim.anim_translate_out_left
            )
        }
        binding.btnShipping.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                startActivity(Intent(requireActivity(), AddressSelectionActivity::class.java))
                requireActivity().overridePendingTransition(
                    R.anim.anim_translate_in_right,
                    R.anim.anim_translate_out_left
                )
            }
        }
    }
}
