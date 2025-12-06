package com.ayvytr.flow.exception

import androidx.annotation.StringRes

/**
 * @author Administrator
 */
open class NetworkException(e: Throwable, @StringRes var stringId: Int = -1, val code: Int = 0):
    Exception(e.message) {

    fun isValidStringId(): Boolean {
        return stringId != -1
    }

}