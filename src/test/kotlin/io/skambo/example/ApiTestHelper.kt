package io.skambo.example

import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import org.springframework.http.HttpHeaders
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

    fun createHttpHeaders(header:Header? = null): HttpHeaders {
        val httpHeaders: HttpHeaders = HttpHeaders()

        httpHeaders.add(ApiHeaderKey.MESSAGE_ID.value, header?.messageId ?: UUID.randomUUID().toString())
        httpHeaders.add(ApiHeaderKey.TIMESTAMP.value, (header?.timestamp ?: OffsetDateTime.now()).toString())
        httpHeaders.add(ApiHeaderKey.GROUP_ID.value, header?.groupId)

        return httpHeaders
    }
}