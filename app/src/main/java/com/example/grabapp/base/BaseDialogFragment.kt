package com.example.grabapp.base

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
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

        dialog?.window?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            val windowParams: WindowManager.LayoutParams = attributes
            windowParams.dimAmount = dimAmount()
            val displayMetrics = context?.resources?.displayMetrics
            windowParams.width =
                (displayMetrics?.widthPixels?.times(width()))?.toInt() ?: windowParams.width
            windowParams.flags = windowParams.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            attributes = windowParams
        }

        setUpInit()
    }


    protected open fun dimAmount(): Float = 0.9f
    protected open fun width(): Float = 0.95f

    fun show(fragmentManager: FragmentManager) {
        if (isAdded || isVisible || isRemoving || isStateSaved) {
            return
        }
        this.show(fragmentManager)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
