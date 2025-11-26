package com.example.grabapp.utils

import android.content.Context
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

class FragmentContainer(context: Context,
): LinearLayout(context) {
    fun replaceFragment(frangment: Fragment){
        this.removeAllViews()
        this.addView(frangment.view)
    }
}