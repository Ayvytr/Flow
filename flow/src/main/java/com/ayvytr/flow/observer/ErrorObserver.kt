package com.ayvytr.flow.observer

import com.ayvytr.flow.base.IView
import com.ayvytr.flow.exception.NetworkException

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.1
 */
interface ErrorObserver {
    fun onError(view: IView?, e: NetworkException) {
        view?.apply {
            if (e.isValidStringId()) {
                showMessage(e.stringId)
            } else {
                showMessage(e.message!!)
            }
        }
    }
}