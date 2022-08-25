package com.ayvytr.flowapp

/**
 * @author admin
 */

fun main() {
    val asc = Array(5) {
        (it * it).toString()
    }
    asc.forEach { println(it) }
}