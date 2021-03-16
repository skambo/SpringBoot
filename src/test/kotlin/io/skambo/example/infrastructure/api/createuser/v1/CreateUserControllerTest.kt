package io.skambo.example.infrastructure.api.createuser.v1

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.ApiTestHelper
import io.skambo.example.common.metrics.MetricsAgent
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserRequest
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest

@ExtendWith(SpringExtension::class)
class CreateUserControllerTest {
    @MockBean
    private lateinit var mockUserService: UserService

    @MockBean
    private lateinit var metricsAgent: MetricsAgent

    @Mock
    private lateinit var testHttpServletRequest: HttpServletRequest

    private lateinit var testCreateUserController: CreateUserController

    private val testUser: User = User(
        name = "Anne",
        dateOfBirth = OffsetDateTime.now(),
        city = "Nairobi",
        email = "anne@gmail.com",
        phoneNumber = "1224"
    )

    private val mockCreatedUser:User = User(
        id = 1L,
        name = "Anne",
        dateOfBirth = OffsetDateTime.now(),
        city = "Nairobi",
        email = "anne@gmail.com",
        phoneNumber = "1224"
    )

    private val testCreateUserRequest: CreateUserRequest = CreateUserRequest(
        header = ApiTestHelper.createTestHeader(),
        name = testUser.name,
        dateOfBirth = testUser.dateOfBirth,
        city = testUser.city,
        email = testUser.email,
        phoneNumber = testUser.phoneNumber
    )

    @BeforeEach
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        testCreateUserController = CreateUserController(mockUserService, metricsAgent)
    }

    @AfterEach
    fun tearDown(){
    }

    @Test
    fun testCreateUser(){
        `when`(mockUserService.createUser(testUser)).thenReturn(mockCreatedUser)

        val expectedResponseBody: CreateUserResponse = CreateUserResponse(
            header = ApiTestHelper.createTestHeader(),
            id = mockCreatedUser.id!!,
            name = mockCreatedUser.name,
            dateOfBirth = mockCreatedUser.dateOfBirth,
            city = mockCreatedUser.city,
            email = mockCreatedUser.email,
            phoneNumber = mockCreatedUser.phoneNumber
        )

        val response: ResponseEntity<CreateUserResponse> = testCreateUserController
            .createUser(testCreateUserRequest, testHttpServletRequest)

        Assert.assertEquals(HttpStatus.CREATED, response.statusCode)
        Assert.assertNotNull(response.body)
        //Using Assertj to ignore the header
        Assertions.assertThat(response.body).usingRecursiveComparison().ignoringFields("header")
            .isEqualTo(expectedResponseBody)
        //We are now asserting the header
        Assert.assertNotNull(response.body?.header?.groupId)
        Assert.assertEquals(ResponseStatus.SUCCESS.value, response.body?.header?.responseStatus?.status)
        Assert.assertNull(response.body?.header?.responseStatus?.errorCode)
        Assert.assertNull(response.body?.header?.responseStatus?.errorMessage)

        verify(mockUserService, times(1 )).createUser(testUser)
    }

    @Test
    fun testCreateUser_DuplicateUserException(){
        val duplicateUserException: DuplicateUserException = DuplicateUserException("User exists")

        `when`(mockUserService.createUser(testUser)).thenThrow(duplicateUserException)

        val thrownException:DuplicateUserException = Assert.assertThrows(DuplicateUserException::class.java){
            testCreateUserController.createUser(testCreateUserRequest, testHttpServletRequest)
        }

        Assert.assertEquals(duplicateUserException, thrownException)
        verify(mockUserService, times(1 )).createUser(testUser)
    }

    @Test
    fun testCreateUser_UnexpectedException_Propagated(){
        val unexpectedException: RuntimeException = RuntimeException("An unexpected error occurred")

        `when`(mockUserService.createUser(testUser)).thenThrow(unexpectedException)

        val thrownException:RuntimeException = Assert.assertThrows(RuntimeException::class.java){
            testCreateUserController.createUser(testCreateUserRequest, testHttpServletRequest)
        }

        Assert.assertEquals(unexpectedException, thrownException)
        verify(mockUserService, times(1 )).createUser(testUser)
    }
}