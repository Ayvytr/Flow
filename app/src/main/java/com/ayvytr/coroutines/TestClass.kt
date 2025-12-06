package com.ayvytr.coroutines

import androidx.collection.intListOf

/**
 * @author Do
 */
class TestClass {
    fun test1(): String? {
        var s: String ?= null
        s = "f"
        return s
    }

    fun f1() {
        val stringLength  = test1()?.length ?: -1
        val string = test1()
        System.out.println(string)

        val s:String = test1()!!

        val list = intListOf(1, 2, 3)

        System.out.println(list.indexOfFirst {
            it == 1
        })

    }

}