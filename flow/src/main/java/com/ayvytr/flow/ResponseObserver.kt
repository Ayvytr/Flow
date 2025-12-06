package com.ayvytr.flow

import com.ayvytr.flow.exception.NetworkException
import com.ayvytr.flow.internal.IView

/**
 * @author Administrator
 */
interface ResponseObserver<T> {
    fun onSuccess(t: T)

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