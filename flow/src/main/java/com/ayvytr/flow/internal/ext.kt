package com.ayvytr.flow.internal

import java.lang.reflect.ParameterizedType

/**
 * 获取vm clazz
 */
@Suppress("UNCHECKED_CAST")
fun <VM> getVmClazz(obj: Any): VM {
    return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as VM
}

internal fun StackTraceElement.toKey(): String {
    return "$className.$methodName@$lineNumber"
}