package com.ayvytr.flow

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import com.ayvytr.flow.exception.NetworkException
import com.ayvytr.flow.internal.IView
import com.ayvytr.logger.L

/**
 * @author Administrator
 */
object BaseConfig {
    @IntRange(from = 1, to = 10)
    var networkRetryCount: Int = 2

    var networkStringIdConverter: (Throwable) -> NetworkException = { e ->
        NetworkException(e, R.string.cannot_connect_server)
    }
}