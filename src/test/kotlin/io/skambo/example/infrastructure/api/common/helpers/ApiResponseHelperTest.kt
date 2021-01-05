package io.skambo.example.infrastructure.api.common.helpers

import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.http.server.ServletServerHttpRequest
import java.net.http.HttpRequest
import java.time.OffsetDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest

class ApiResponseHelperTest {
    @Mock
    private lateinit var testHttpRequest: HttpServletRequest

    @BeforeEach
    fun setUp(){
        MockitoAnnotations.initMocks(this)
    }

    @AfterEach
    fun tearDown(){
    }

    @Test
    fun testCreateSuccessHeader(){
        val testHeader: Header = Header(
            messageId = UUID.randomUUID().toString(),
            groupId = UUID.randomUUID().toString(),
            timestamp = OffsetDateTime.now(),
            responseStatus = Status(status = ResponseStatus.SUCCESS.value ))
        val response: Header = ApiResponseHelper.createSuccessHeader(testHttpRequest, testHeader)

        // Assert.assertEquals(testHeader.messageId, response.messageId)
    }
}