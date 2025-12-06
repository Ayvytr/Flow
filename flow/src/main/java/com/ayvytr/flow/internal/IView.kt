package com.ayvytr.flow.internal

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 */
interface IView {
    fun initView(savedInstanceState: Bundle?)
    fun initData(savedInstanceState: Bundle?)
    fun showLoading(isShow: Boolean = true)
    fun showMessage(@StringRes strId: Int)
    fun showMessage(message: String)
}
