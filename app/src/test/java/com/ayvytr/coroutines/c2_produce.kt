package com.ayvytr.coroutines

import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * @author Admin
 */

class c2 {
    @Test
    fun testProduce() = runBlocking {
        // 生产者：启动协程，源源不断发数据
        val channel = produce {
            for (i in 1..3) {
                delay(300)
                send(i) // 发送数据到通道
                println("生产了: $i")
            }
        }

        // 消费者：遍历接收所有数据
        for (num in channel) {
            println("收到了: $num")
        }

        println("全部接收完毕")
    }
}