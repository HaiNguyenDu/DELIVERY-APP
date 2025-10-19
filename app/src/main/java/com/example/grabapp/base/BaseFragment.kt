package com.example.grabapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding, V : BaseViewModel> : Fragment() {
    var isCreated = false
    protected val binding by getLazyBinding()
    protected val viewModel by getLazyViewModel()
    abstract fun getLazyBinding(): Lazy<T>
    abstract fun getLazyViewModel(): Lazy<V>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCreated = true
        setUpClick()
        handleInset()
    }

    private fun handleInset() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomInset = maxOf(systemBarInsets.bottom, imeInsets.bottom)
            binding.root.setPadding(
                0,
                0,
                0,
                bottomInset
            )
            handleInset(binding.root, systemBarInsets, bottomInset)
            WindowInsetsCompat.CONSUMED
        }
    }

    open fun handleInset(view: View, inset: Insets, bottomInset: Int) {
        view.setPadding(inset.left, inset.top, inset.right, bottomInset)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isCreated = false
    }

    open fun setBackPress() {

    }

    abstract fun setUpClick()
}