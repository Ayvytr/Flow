package com.ayvytr.flow.exception

import androidx.annotation.StringRes

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.1
 */
class NetworkException(e: Throwable, @StringRes var stringId: Int = -1, var code: Int = 0):
    Exception(e.message) {

    fun isValidStringId(): Boolean {
        return stringId != -1
    }

}