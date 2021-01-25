package io.skambo.example.infrastructure.api.updateuser.v1

import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.ApiTestHelper
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.updateuser.v1.dto.UpdateUserRequest
import io.skambo.example.infrastructure.api.updateuser.v1.dto.UpdateUserResponse
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest

@ExtendWith(SpringExtension::class)
class UpdateUserControllerTest {
    @MockBean
    private lateinit var mockUserService: UserService

    @Mock
    private lateinit var testHttpServletRequest: HttpServletRequest

    private lateinit var testUpdateUserController: UpdateUserController

    private val userId: Long = 1L

    private val existingUser: User = User(
        id = userId,
        name = "Bob",
        dateOfBirth = OffsetDateTime.now(),
        city = "Mombasa",
        email = "anne@gmail.com",
        phoneNumber = "1224"
    )

    private val mockUpdatedUser: User = User(
        id = userId,
        name = "Anne",
        dateOfBirth = OffsetDateTime.now(),
        city = "Nairobi",
        email = "anne@gmail.com",
        phoneNumber = "1224"
    )

    private val testUpdateUserRequest: UpdateUserRequest = UpdateUserRequest(
        header = ApiTestHelper.createTestHeader(),
        name = mockUpdatedUser.name,
        dateOfBirth = mockUpdatedUser.dateOfBirth,
        city = mockUpdatedUser.city
    )

    @BeforeEach
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        testUpdateUserController = UpdateUserController(mockUserService)
    }

    @AfterEach
    fun tearDown(){
    }

    @Test
    fun testUpdateUser(){
        `when`(mockUserService.findUserById(userId.toString())).thenReturn(existingUser)
        doNothing().`when`(mockUserService).updateUser(mockUpdatedUser)

        val actualResponse: ResponseEntity<UpdateUserResponse> = testUpdateUserController
            .updateUser(testUpdateUserRequest, userId.toString(), testHttpServletRequest)

        Assert.assertEquals(HttpStatus.OK, actualResponse.statusCode)
        Assert.assertNotNull(actualResponse.body)
        //Using Assertj to ignore the header
        Assertions.assertThat(actualResponse.body).usingRecursiveComparison().ignoringFields("header")
            .isEqualTo(mockUpdatedUser)
        //We are now asserting the header
        Assert.assertNotNull(actualResponse.body?.header?.groupId)
        Assert.assertEquals(ResponseStatus.SUCCESS.value, actualResponse.body?.header?.responseStatus?.status)
        Assert.assertNull(actualResponse.body?.header?.responseStatus?.errorCode)
        Assert.assertNull(actualResponse.body?.header?.responseStatus?.errorMessage)

        verify(mockUserService, times(1 )).findUserById(userId.toString())
        verify(mockUserService, times(1 )).updateUser(mockUpdatedUser)
    }

    @Test
    fun testUpdateUser_InvalidUserId_ThrowsUserNotFoundException(){
        val userNotFoundException: UserNotFoundException = UserNotFoundException("User not found")

       `when`(mockUserService.findUserById(userId.toString())).thenThrow(userNotFoundException)

        val thrownException: UserNotFoundException = Assert.assertThrows(UserNotFoundException::class.java){
            testUpdateUserController.updateUser(testUpdateUserRequest, userId.toString(), testHttpServletRequest)
        }

        Assert.assertEquals(userNotFoundException, thrownException)

        verify(mockUserService, times(1)).findUserById(userId.toString())
        verify(mockUserService, never()).updateUser(mockUpdatedUser)
    }

    @Test
    fun testUpdateUser_UnexpectedException_Propagated(){
        val unexpectedException: RuntimeException = RuntimeException("An unexpected error occurred")

        `when`(mockUserService.findUserById(userId.toString())).thenThrow(unexpectedException)

        val thrownException:RuntimeException = Assert.assertThrows(RuntimeException::class.java){
            testUpdateUserController.updateUser(testUpdateUserRequest, userId.toString(), testHttpServletRequest)
        }

        Assert.assertEquals(unexpectedException, thrownException)
        verify(mockUserService, times(1)).findUserById(userId.toString())
        verify(mockUserService, never()).updateUser(mockUpdatedUser)
    }
}