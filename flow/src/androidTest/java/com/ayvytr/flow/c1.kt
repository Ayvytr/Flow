package com.ayvytr.flow

import android.app.Application
import android.content.ComponentCallbacks2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * @author Admin
 */
class MyApp : Application(), CoroutineScope by MainScope() {

    override fun onCreate() {
        super.onCreate()
        // 直接使用 launch 启动协程
        launch {
            // 全局任务
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        cancel() // 低内存时取消
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // 只要是后台高内存修剪，直接释放
        if (level >= TRIM_MEMORY_BACKGROUND) {
            cancel()
        }
    }
}