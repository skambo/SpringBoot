package io.skambo.example.application.services

import io.skambo.example.application.domain.model.User
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime

@ExtendWith(SpringExtension::class)
class UserServiceTest {
    @MockBean
    private lateinit var mockUserRepository: UserRepository

    private lateinit var testUserService: UserService

    // This is setting up the test data
    private val testUser: User = User(
        name = "Anne",
        dateOfBirth = OffsetDateTime.now(),
        city = "Nairobi",
        email = "anne@gmail.com",
        phoneNumber = "1224")

    private val testUserDataModel:UserDataModel = UserDataModel(
        id = 1L,
        name = testUser.name,
        dateOfBirth = testUser.dateOfBirth.toString(),
        city = testUser.city,
        email = testUser.email,
        phoneNumber = testUser.phoneNumber
    )

    @BeforeEach
    fun setUp(){
        MockitoAnnotations.initMocks(this)

       testUserService = UserService(mockUserRepository)
    }

    @AfterEach
    fun tearDown(){
    }

    @Test
    fun testCreate(){
       `when`(mockUserRepository.save(Mockito.any(UserDataModel::class.java))).thenReturn(testUserDataModel)
        val expectedResponse: User = User(
            id = testUserDataModel.id,
            name = testUser.name,
            dateOfBirth = testUser.dateOfBirth,
            city = testUser.city,
            email = testUser.email,
            phoneNumber = testUser.phoneNumber)

        val actualResponse = testUserService.create(testUser)
        Assert.assertEquals(expectedResponse, actualResponse)
    }
}