package io.skambo.example.infrastructure.api.deleteuser.v1

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.deleteuser.v1.dto.DeleteUserResponse
import org.junit.Assert
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest

@ExtendWith(SpringExtension::class)
class DeleteUserControllerTest {
    @MockBean
    private lateinit var mockUserService: UserService

    @Mock
    private lateinit var testHttpServletRequest: HttpServletRequest

    private lateinit var testDeleteUserController: DeleteUserController

    private val userId: Long = 1L

    private val messageId: String = UUID.randomUUID().toString()

    private val mockUser: User = User(
        id = userId,
        name = "Anne",
        dateOfBirth = OffsetDateTime.now(),
        city = "Nairobi",
        email = "anne@gmail.com",
        phoneNumber = "1224"
    )

    @BeforeEach
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        testDeleteUserController = DeleteUserController(mockUserService)

        `when`(testHttpServletRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value)).thenReturn(messageId)
        `when`(testHttpServletRequest.getHeader(ApiHeaderKey.TIMESTAMP.value)).thenReturn(OffsetDateTime.now().toString())
    }

    @AfterEach
    fun tearDown(){
    }

    @Test
    fun testDeleteUser(){
        doNothing().`when`(mockUserService).deleteUser(userId)
        val actualResponse: ResponseEntity<DeleteUserResponse> = testDeleteUserController.deleteUser(userId, testHttpServletRequest)

        Assert.assertEquals(HttpStatus.OK, actualResponse.statusCode)
        Assert.assertNotNull(actualResponse.body)

        val responseBody: DeleteUserResponse = actualResponse.body!!

        Assert.assertNotNull(responseBody.header.messageId)
        Assert.assertNotNull(responseBody.header.timestamp)

        verify(mockUserService, Mockito.times(1)).deleteUser(userId)
    }

    @Test
    fun testDeleteUser_InvalidUserId_ReturnsNotFoundError(){
        val userNotFoundException: UserNotFoundException = UserNotFoundException("User not found")

        doThrow(userNotFoundException).`when`(mockUserService).deleteUser(userId)

        val thrownException: UserNotFoundException = Assert.assertThrows(UserNotFoundException::class.java){
            testDeleteUserController.deleteUser(userId, testHttpServletRequest)
        }

        Assert.assertEquals(userNotFoundException, thrownException)

        verify(mockUserService, Mockito.times(1)).deleteUser(userId)
    }

    @Test
    fun testDeleteUser_UnexpectedException_Propagated(){
        val unexpectedException: RuntimeException = RuntimeException("An unexpected error occurred")

        `when`(mockUserService.deleteUser(userId)).thenThrow(unexpectedException)

        val thrownException:RuntimeException = Assert.assertThrows(RuntimeException::class.java){
            testDeleteUserController.deleteUser(userId, testHttpServletRequest)
        }

        Assert.assertEquals(unexpectedException, thrownException)
        verify(mockUserService, Mockito.times(1)).deleteUser(userId)
    }
}