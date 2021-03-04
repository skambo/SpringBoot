package io.skambo.example.infrastructure.utils.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.skambo.example.infrastructure.utils.gson.deserializers.OffsetDateTimeDeserializer
import io.skambo.example.infrastructure.utils.gson.serializers.OffsetDateTimeSerializer
import java.time.OffsetDateTime

/**
 * Gson factory that returns a Gson instance that is appropriately configured
 */
object GsonFactory {
    fun getGsonInstance(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeSerializer())
            .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeDeserializer())
            .create()
    }
}