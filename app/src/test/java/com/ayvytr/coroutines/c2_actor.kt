package com.ayvytr.coroutines

import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * @author Admin
 */
class c2_actor {
    @Test
    fun t1() {
        runBlocking {
            // 创建 Actor
            val actor = actor<String> {
                for (message in channel) {
                    println("收到消息: $message")
                }
            }

            // 发送消息
            actor.send("Hello")
            actor.send("World")

            // 关闭 Actor
            actor.close()
        }
    }
}