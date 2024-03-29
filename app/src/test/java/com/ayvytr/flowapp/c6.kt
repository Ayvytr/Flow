package com.ayvytr.flowapp

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @author admin
 */

fun main() = runBlocking {
    val job = GlobalScope.launch {
        delay(1000)
        println("World!")
    }

    println("Hello, ")
    job.join()
}
