package io.skambo.example.infrastructure.api.createuser.v1

import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserRequest
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime

@ExtendWith(SpringExtension::class)
class CreateUserControllerTest {
    @MockBean
    private lateinit var mockUserService: UserService

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
        name = testUser.name,
        dateOfBirth = testUser.dateOfBirth,
        city = testUser.city,
        email = testUser.email,
        phoneNumber = testUser.phoneNumber
    )

    @BeforeEach
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        testCreateUserController = CreateUserController(mockUserService)
    }

    @AfterEach
    fun tearDown(){
    }

    @Test
    fun testCreateUser(){
        //TODO Test response conforms to API standards

        `when`(mockUserService.createUser(testUser)).thenReturn(mockCreatedUser)

        val expectedResponseBody: CreateUserResponse = CreateUserResponse(
            id = mockCreatedUser.id!!,
            name = mockCreatedUser.name,
            dateOfBirth = mockCreatedUser.dateOfBirth,
            city = mockCreatedUser.city,
            email = mockCreatedUser.email,
            phoneNumber = mockCreatedUser.phoneNumber
        )

        val response: ResponseEntity<CreateUserResponse> = testCreateUserController.createUser(testCreateUserRequest)

        Assert.assertEquals(HttpStatus.CREATED, response.statusCode)
        Assert.assertNotNull(response.body)
        Assert.assertEquals(expectedResponseBody, response.body)
    }
}