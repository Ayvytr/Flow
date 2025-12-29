package com.ayvytr.flow.internal

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.1
 */
internal interface BaseLifecycleObserver : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreateEvent()
}