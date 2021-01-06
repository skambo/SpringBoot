package io.skambo.example.infrastructure.api.fetchuser.v1

import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.deleteuser.v1.dto.DeleteUserResponse
import io.skambo.example.infrastructure.api.fetchuser.v1.dto.FetchUserResponse
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
class FetchUserControllerTest {
    @MockBean
    private lateinit var mockUserService: UserService

    @Mock
    private lateinit var testHttpServletRequest: HttpServletRequest

    private lateinit var testFetchUserController: FetchUserController

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
        testFetchUserController = FetchUserController(mockUserService)

        `when`(testHttpServletRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value)).thenReturn(messageId)
        `when`(testHttpServletRequest.getHeader(ApiHeaderKey.TIMESTAMP.value)).thenReturn(OffsetDateTime.now().toString())
    }

    @AfterEach
    fun tearDown(){
    }

    @Test
    fun testFetchUser(){
        `when`(mockUserService.findUserById(userId)).thenReturn(mockUser)
         val actualResponse:ResponseEntity<FetchUserResponse> = testFetchUserController.fetchUser(userId, testHttpServletRequest)

        Assert.assertEquals(HttpStatus.OK, actualResponse.statusCode)
        Assert.assertNotNull(actualResponse.body)
        val responseBody: FetchUserResponse = actualResponse.body!!

        Assert.assertNotNull(responseBody.header.messageId)
        Assert.assertNotNull(responseBody.header.timestamp)
        Assert.assertEquals(userId, responseBody.id)
        Assert.assertEquals(mockUser.name, responseBody.name)
        Assert.assertEquals(mockUser.city, responseBody.city)
        Assert.assertEquals(mockUser.dateOfBirth, responseBody.dateOfBirth)
        Assert.assertEquals(mockUser.email, responseBody.email)
        Assert.assertEquals(mockUser.phoneNumber, responseBody.phoneNumber)

        verify(mockUserService, Mockito.times(1)).findUserById(userId)
    }

    @Test
    fun testFetchUser_InvalidUserId_ReturnsNotFoundError(){
        val userNotFoundException: UserNotFoundException = UserNotFoundException("User not found")

        doThrow(userNotFoundException).`when`(mockUserService).findUserById(userId)

        val thrownException: UserNotFoundException = Assert.assertThrows(UserNotFoundException::class.java){
            testFetchUserController.fetchUser(userId, testHttpServletRequest)
        }

        Assert.assertEquals(userNotFoundException, thrownException)

        verify(mockUserService, Mockito.times(1)).findUserById(userId)
    }

    @Test
    fun testFetchUser_UnexpectedException_Propagated(){
        val unexpectedException: RuntimeException = RuntimeException("An unexpected error occurred")

        `when`(mockUserService.findUserById(userId)).thenThrow(unexpectedException)

        val thrownException:RuntimeException = Assert.assertThrows(RuntimeException::class.java){
            testFetchUserController.fetchUser(userId, testHttpServletRequest)
        }

        Assert.assertEquals(unexpectedException, thrownException)
        verify(mockUserService, Mockito.times(1)).findUserById(userId)
    }
}