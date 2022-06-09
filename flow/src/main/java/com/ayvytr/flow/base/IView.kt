package com.ayvytr.flow.base

import android.os.Bundle
import androidx.annotation.StringRes

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.1
 */
interface IView {
    fun initView(savedInstanceState: Bundle?)
    fun initData(savedInstanceState: Bundle?)
    fun showLoading(isShow: Boolean = true)
    fun showMessage(@StringRes strId: Int)
    fun showMessage(message: CharSequence)
}
