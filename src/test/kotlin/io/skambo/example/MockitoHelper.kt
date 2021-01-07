package io.skambo.example

import org.mockito.Mockito

object MockitoHelper {
    fun <T> anyObject(type: Class<T>): T {
        Mockito.any<T>(type)
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(): T =  null as T

    fun <T> eq(obj: T): T = Mockito.eq<T>(obj)
}