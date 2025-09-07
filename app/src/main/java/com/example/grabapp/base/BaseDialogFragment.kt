package com.example.grabapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.grabapp.R

abstract class BaseDialogFragment<viewBinding : ViewBinding> : DialogFragment() {
    private var _binding: viewBinding? = null
    protected val binding get() = _binding!!        

    abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): viewBinding

    abstract fun setUpInit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpInit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}