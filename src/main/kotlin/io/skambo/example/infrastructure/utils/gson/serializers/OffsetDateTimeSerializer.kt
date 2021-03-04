package io.skambo.example.infrastructure.utils.gson.serializers

import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.OffsetDateTime

/**
 * Custom serializer that handles an OffsetDateTime
 */
class OffsetDateTimeSerializer: JsonSerializer<OffsetDateTime> {
    override fun serialize(src: OffsetDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonPrimitive {
        return JsonPrimitive(src.toString());
    }
}