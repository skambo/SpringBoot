package io.skambo.example.infrastructure.api.exceptions

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.ApiErrorResponse
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @Test
    fun testHandleUserNotFoundException(){
        val userNotFoundException: UserNotFoundException = UserNotFoundException("User not found")
        val responseEntity: ResponseEntity<ApiErrorResponse> = testRestResponseEntityExceptionHandler
            .handleUserNotFoundException(userNotFoundException, testHttpServletRequest)

        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)

        val responseBody: ApiErrorResponse? = responseEntity.body
        val expectedErrorCode: String = "SpringBootExample.UserNotFound"
        val expectedErrorMessage: String = "User not found"

        Assert.assertNotNull(responseBody)
        Assert.assertEquals(ResponseStatus.REJECTED.value, responseBody?.header?.responseStatus?.status)
        Assert.assertEquals(expectedErrorCode, responseBody?.header?.responseStatus?.errorCode)
        Assert.assertEquals(expectedErrorMessage, responseBody?.header?.responseStatus?.errorMessage)
    }

    @Test
    fun testHandleInternalServerError(){
        val unexpectedException: RuntimeException = RuntimeException("Unexpected failure")
        val responseEntity: ResponseEntity<ApiErrorResponse> = testRestResponseEntityExceptionHandler
            .handleInternalServerError(unexpectedException, testHttpServletRequest)

        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.statusCode)

        val responseBody: ApiErrorResponse? = responseEntity.body
        val expectedErrorCode: String = "SpringBootExample.UnknownFailure"
        val expectedErrorMessage: String = "An unexpected failure has occurred with details: ${unexpectedException.localizedMessage}"

        Assert.assertNotNull(responseBody)
        Assert.assertEquals(ResponseStatus.FAILURE.value, responseBody?.header?.responseStatus?.status)
        Assert.assertEquals(expectedErrorCode, responseBody?.header?.responseStatus?.errorCode)
        Assert.assertEquals(expectedErrorMessage, responseBody?.header?.responseStatus?.errorMessage)
    }
}