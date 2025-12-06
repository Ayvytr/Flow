package com.ayvytr.coroutines.test

import androidx.lifecycle.ViewModel
import com.ayvytr.logger.L

/**
 * @author Administrator
 */
class TestViewModel : ViewModel() {
    override fun onCleared() {
        L.e()
    }

    fun show() {
        L.e()
    }
}