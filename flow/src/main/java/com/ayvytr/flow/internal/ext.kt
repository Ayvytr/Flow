package com.ayvytr.flow.internal

import java.lang.reflect.ParameterizedType

/**
 * @author Ayvytr ['s GitHub](https://github.com/Ayvytr)
 * @since 0.0.1
 */

/**
 * 获取vm clazz.
 * getGenericSuperclass() 通过反射获取当前类表示的实体（类，接口，基本类型或void）的直接父类的Type，
 * getActualTypeArguments()返回参数数组。
 */
inline fun <reified VM> getVmClass(obj: Any): VM {
    val types = (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
    types.forEach {
        if(it is VM) {
            return it
        }
    }
    throw TypeCastException("Cannot find type generic type VM")
}

internal fun StackTraceElement.toKey(): String {
    return "$className.$methodName@$lineNumber"
}