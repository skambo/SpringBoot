package io.skambo.example.application.services

import io.skambo.example.application.ApplicationTestHelper
import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.application.domain.model.User
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
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
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.util.*
import kotlin.RuntimeException

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
    private val expectedCreatedUserDataModel: UserDataModel = UserDataModel(
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
    fun testCreateUser(){
        `when`(mockUserRepository.findByEmail(testUser.email)).thenReturn(Optional.empty())
        `when`(mockUserRepository.findByPhoneNumber(testUser.phoneNumber)).thenReturn(Optional.empty())
        `when`(mockUserRepository.save(expectedCreatedUserDataModel)).thenReturn(testUserDataModel)

        val expectedResponse: User = User(
            id = testUserDataModel.id,
            name = testUser.name,
            dateOfBirth = testUser.dateOfBirth,
            city = testUser.city,
            email = testUser.email,
            phoneNumber = testUser.phoneNumber
        )

        val actualResponse = testUserService.createUser(testUser)
        Assert.assertEquals(expectedResponse, actualResponse)
        verify(mockUserRepository, times(1)).findByEmail(testUser.email)
        verify(mockUserRepository, times(1)).findByPhoneNumber(testUser.phoneNumber)
        verify(mockUserRepository, times(1)).save(expectedCreatedUserDataModel)
    }

    @Test
    fun testCreate_WithDuplicateEmailField_ThrowsDuplicateException(){
        `when`(mockUserRepository.findByEmail(testUser.email)).thenReturn(Optional.of(testUserDataModel))
        `when`(mockUserRepository.findByPhoneNumber(testUser.phoneNumber)).thenReturn(Optional.empty())

        Assert.assertThrows(DuplicateUserException::class.java){
            testUserService.createUser(testUser)
        }
        verify(mockUserRepository, times(1)).findByEmail(testUser.email)
        verify(mockUserRepository, times(0)).findByPhoneNumber(testUser.phoneNumber)
        verify(mockUserRepository, times(0)).save(expectedCreatedUserDataModel)
    }

    @Test
    fun testCreate_WithDuplicatePhoneNumberField_ThrowsDuplicateException(){
        `when`(mockUserRepository.findByEmail(testUser.email)).thenReturn(Optional.empty())
        `when`(mockUserRepository.findByPhoneNumber(testUser.phoneNumber)).thenReturn(Optional.of(testUserDataModel))

        Assert.assertThrows(DuplicateUserException::class.java){
            testUserService.createUser(testUser)
        }
        verify(mockUserRepository, times(1)).findByEmail(testUser.email)
        verify(mockUserRepository, times(1)).findByPhoneNumber(testUser.phoneNumber)
        verify(mockUserRepository, times(0)).save(expectedCreatedUserDataModel)
    }

    @Test
    fun testCreate_UnexpectedExceptions_ArePropagated(){
        val unexpectedException: RuntimeException = RuntimeException("Runtime exception")

        `when`(mockUserRepository.findByEmail(testUser.email)).thenReturn(Optional.empty())
        `when`(mockUserRepository.findByPhoneNumber(testUser.phoneNumber)).thenReturn(Optional.empty())
        `when`(mockUserRepository.save(expectedCreatedUserDataModel)).thenThrow(unexpectedException)

        val thrownException = Assert.assertThrows(RuntimeException::class.java){
            testUserService.createUser(testUser)
        }
        Assert.assertEquals(unexpectedException, thrownException)

        verify(mockUserRepository, times(1)).findByEmail(testUser.email)
        verify(mockUserRepository, times(1)).findByPhoneNumber(testUser.phoneNumber)
        verify(mockUserRepository, times(1)).save(expectedCreatedUserDataModel)
    }

    @Test
    fun testUpdateUser(){
        val expectedUserDataModel: UserDataModel = UserDataModel(
            name = testUser.name,
            dateOfBirth = testUser.dateOfBirth.toString(),
            city = testUser.city,
            email = testUser.email,
            phoneNumber = testUser.phoneNumber
        )

        `when`(mockUserRepository.save(expectedUserDataModel)).thenReturn(testUserDataModel)
         testUserService.updateUser(testUser)

         verify(mockUserRepository, times(1)).save(expectedUserDataModel)
    }

    @Test
    fun testUpdateUser_UnexpectedExceptions_ArePropagated(){
        val unexpectedException: RuntimeException = RuntimeException("Runtime exception")
        `when`(mockUserRepository.save(expectedCreatedUserDataModel)).thenThrow(unexpectedException)

        val thrownException = Assert.assertThrows(RuntimeException::class.java){
            testUserService.createUser(testUser)
        }
        Assert.assertEquals(unexpectedException, thrownException)
        verify(mockUserRepository, times(1)).save(expectedCreatedUserDataModel)
    }

    @Test
    fun testFindUsers(){
        val pageNumber: Int = 1
        val pageSize: Int = 25
        val sortDirection: String = "desc"
        val sortFields: List<String> = listOf("name", "age", "city")
        val pageRequest:PageRequest = ApplicationTestHelper.createTestPageRequest(pageNumber, pageSize, sortDirection, sortFields)

        val filters: String = ""
        val specification: Specification<UserDataModel>? = ApplicationTestHelper.createTestSpecification(filters)
        val testPage:Page<UserDataModel> = PageImpl<UserDataModel>(listOf(testUserDataModel))

        `when`(mockUserRepository.findAll(pageable = pageRequest, specification = specification)).thenReturn(testPage)

        val expectedResponse:Page<User> = PageImpl<User>(
            listOf(
                User(
                    id = testUserDataModel.id,
                    name = testUserDataModel.name,
                    dateOfBirth = OffsetDateTime.parse(testUserDataModel.dateOfBirth),
                    city = testUserDataModel.city,
                    email = testUserDataModel.email,
                    phoneNumber = testUserDataModel.phoneNumber
                )
            )
        )
        val actualResponse = testUserService.findUsers(pageRequest, filters)

        Assert.assertEquals(expectedResponse, actualResponse)

        verify(mockUserRepository, times(1)).findAll(pageable = pageRequest, specification = specification)
    }

    @Test
    fun testFindUsers_NoUsers_ReturnsEmptyPage(){
        val pageNumber: Int = 1
        val pageSize: Int = 25
        val sortDirection: String = "desc"
        val sortFields: List<String> = listOf("name", "age", "city")
        val pageRequest:PageRequest = ApplicationTestHelper.createTestPageRequest(pageNumber, pageSize, sortDirection, sortFields)

        val filters: String = ""
        val specification: Specification<UserDataModel>? = ApplicationTestHelper.createTestSpecification(filters)
        val testPage:Page<UserDataModel> = PageImpl<UserDataModel>(listOf())

        `when`(mockUserRepository.findAll(pageable = pageRequest, specification = specification)).thenReturn(testPage)

        val expectedResponse:Page<User> = PageImpl<User>(
            listOf()
        )
        val actualResponse = testUserService.findUsers(pageRequest, filters)

        Assert.assertEquals(expectedResponse, actualResponse)

        verify(mockUserRepository, times(1)).findAll(pageable = pageRequest, specification = specification)
    }

    @Test
    fun testFindUsers_UnexpectedExceptions_ArePropagated(){
        val pageNumber: Int = 1
        val pageSize: Int = 25
        val sortDirection: String = "desc"
        val sortFields: List<String> = listOf("name", "age", "city")
        val pageRequest:PageRequest = ApplicationTestHelper.createTestPageRequest(pageNumber, pageSize, sortDirection, sortFields)

        val filters: String = ""
        val specification: Specification<UserDataModel>? = ApplicationTestHelper.createTestSpecification(filters)

        val unexpectedException: RuntimeException = RuntimeException("Runtime exception")
        `when`(mockUserRepository.findAll(pageable = pageRequest, specification = specification)).thenThrow(unexpectedException)

        val thrownException = Assert.assertThrows(RuntimeException::class.java){
            testUserService.findUsers(pageRequest, filters)
        }
        Assert.assertEquals(unexpectedException, thrownException)
        verify(mockUserRepository, times(1)).findAll(pageable = pageRequest, specification = specification)
    }

    @Test
    fun testFindUserById(){
        val userId: Long = 1L
        `when`(mockUserRepository.findById(userId)).thenReturn(Optional.of(testUserDataModel))

        val expectedResponse:User = User(
            id = testUserDataModel.id,
            name = testUserDataModel.name,
            dateOfBirth = OffsetDateTime.parse(testUserDataModel.dateOfBirth),
            city = testUserDataModel.city,
            email = testUserDataModel.email,
            phoneNumber = testUserDataModel.phoneNumber
        )
        val actualResponse:User = testUserService.findUserById(userId)
        Assert.assertEquals(expectedResponse, actualResponse)
        verify(mockUserRepository, times(1)).findById(userId)
    }

    @Test
    fun testFindUserById_InvalidUserId_ThrowsUserNotFoundException(){
        val userId: Long = 1L
        `when`(mockUserRepository.findById(userId)).thenReturn(Optional.empty())

        val thrownException = Assert.assertThrows(UserNotFoundException::class.java){
            testUserService.findUserById(userId)
        }

        val expectedExceptionMessage: String = "User with id $userId not found"
        Assert.assertEquals(expectedExceptionMessage, thrownException.message)

        verify(mockUserRepository, times(1)).findById(userId)
    }

    @Test
    fun testFindUserById_UnexpectedExceptions_ArePropagated(){
        val userId: Long = 1L
        val unexpectedException: RuntimeException = RuntimeException("Runtime exception")
        `when`(mockUserRepository.findById(userId)).thenThrow(unexpectedException)

        val thrownException = Assert.assertThrows(RuntimeException::class.java){
            testUserService.findUserById(userId)
        }

        Assert.assertEquals(unexpectedException, thrownException)

        verify(mockUserRepository, times(1)).findById(userId)
    }

    @Test
    fun testDeleteUser(){
        val userId: Long = 1L
        `when`(mockUserRepository.findById(userId)).thenReturn(Optional.of(testUserDataModel))
        doNothing().`when`(mockUserRepository).delete(testUserDataModel)

        testUserService.deleteUser(userId)
        verify(mockUserRepository, times(1)).delete(testUserDataModel)
    }

    @Test
    fun testDeleteUser_InvalidUserId_ThrowsUserNotFoundException(){
        val userId: Long = 1L
        `when`(mockUserRepository.findById(userId)).thenReturn(Optional.empty())
        doNothing().`when`(mockUserRepository).delete(testUserDataModel)

        val thrownException = Assert.assertThrows(UserNotFoundException::class.java){
            testUserService.deleteUser(userId)
        }

        val expectedExceptionMessage: String = "User with id $userId not found"
        Assert.assertEquals(expectedExceptionMessage, thrownException.message)

        verify(mockUserRepository, times(1)).findById(userId)
        verify(mockUserRepository, times(0 )).delete(testUserDataModel)
    }

    @Test
    fun testDeleteUser_UnexpectedExceptions_ArePropagated(){
        val userId: Long = 1L
        val unexpectedException: RuntimeException = RuntimeException("Runtime exception")
        `when`(mockUserRepository.findById(userId)).thenReturn(Optional.of(testUserDataModel))
        `when`(mockUserRepository.delete(testUserDataModel)).thenThrow(unexpectedException)

        val thrownException = Assert.assertThrows(RuntimeException::class.java){
            testUserService.deleteUser(userId)
        }

        Assert.assertEquals(unexpectedException, thrownException)

        verify(mockUserRepository, times(1)).findById(userId)
        verify(mockUserRepository, times(1 )).delete(testUserDataModel)
    }
}