package io.skambo.example.infrastructure.api

import io.skambo.example.infrastructure.api.common.dto.v1.Header
import java.time.OffsetDateTime
import java.util.*

object ApiTestHelper {
    fun createTestHeader(): Header{
        return Header(
            messageId = UUID.randomUUID().toString(),
            timestamp = OffsetDateTime.now(),
            groupId = UUID.randomUUID().toString()
        )
    }
}