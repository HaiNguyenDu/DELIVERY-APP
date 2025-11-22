package com.example.grabapp.driver.confirm_driver.pager

import androidx.annotation.StringRes
import com.example.grabapp.R

enum class ConfirmPager(
    @StringRes
    val pageStepRes: Int,
    @StringRes
    val pageTitleRes: Int,
    @StringRes
    val pageContentRes: Int,
    val confirmationStatus: ConfirmationStatus
) {
    CONFIRM_PAGE_01(
        R.string.confirm_pager1_step,
        R.string.confirm_pager1_title,
        R.string.confirm_pager1_content,
        ConfirmationStatus.NONE
    ),
    CONFIRM_PAGE_02(
        R.string.confirm_pager2_step,
        R.string.confirm_pager2_title,
        R.string.confirm_pager2_content,
        ConfirmationStatus.NONE
    ),
    CONFIRM_PAGE_03(
        R.string.confirm_pager3_step,
        R.string.confirm_pager3_title,
        R.string.confirm_pager3_content,
        ConfirmationStatus.NONE
    ),
    CONFIRM_PAGE_04(
        R.string.confirm_pager4_step,
        R.string.confirm_pager4_title,
        R.string.confirm_pager4_content,
        ConfirmationStatus.NONE
    )
}

enum class ConfirmationStatus {
    YES, NO, NONE
}
