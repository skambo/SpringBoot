package io.skambo.example.infrastructure.api.exceptions

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.ApiErrorResponse
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.OffsetDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest

class RestResponseEntityExceptionHandlerTest {
    @Mock
    private lateinit var testHttpServletRequest: HttpServletRequest

    private lateinit var testRestResponseEntityExceptionHandler: RestResponseEntityExceptionHandler

    @BeforeEach
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        testRestResponseEntityExceptionHandler = RestResponseEntityExceptionHandler()
    }

    @AfterEach
    fun tearDown(){
    }

    @Test
    fun testHandleDuplicateUserException(){
        val duplicateUserException: DuplicateUserException = DuplicateUserException("Duplicate user")
        val responseHeader: Header = Header(
            messageId = UUID.randomUUID().toString(),
            timestamp = OffsetDateTime.now(),
            responseStatus = Status(
                status = ResponseStatus.REJECTED.value
            )
        )
        val responseEntity: ResponseEntity<ApiErrorResponse> = testRestResponseEntityExceptionHandler
            .handleDuplicateUserException(duplicateUserException, testHttpServletRequest)

        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        val responseBody: ApiErrorResponse? = responseEntity.body
        val expectedErrorCode: String = "SpringBootExample.DuplicateUser"
        val expectedErrorMessage: String = "There is an existing user with the provided details"

        Assert.assertNotNull(responseBody)
        Assert.assertEquals(ResponseStatus.REJECTED.value, responseBody?.header?.responseStatus?.status)
        Assert.assertEquals(expectedErrorCode, responseBody?.header?.responseStatus?.errorCode)
        Assert.assertEquals(expectedErrorMessage, responseBody?.header?.responseStatus?.errorMessage)
    }
}