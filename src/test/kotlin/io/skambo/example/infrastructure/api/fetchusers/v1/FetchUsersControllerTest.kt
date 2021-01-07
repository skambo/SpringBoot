package io.skambo.example.infrastructure.api.fetchusers.v1

import io.skambo.example.MockitoHelper
import io.skambo.example.TestHelper
import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.common.dto.v1.UserDTO
import io.skambo.example.infrastructure.api.fetchuser.v1.dto.FetchUserResponse
import io.skambo.example.infrastructure.api.fetchusers.v1.dto.FetchUsersResponse
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
import org.mockito.Mockito.eq
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.util.*
import javax.servlet.http.HttpServletRequest

@ExtendWith(SpringExtension::class)
class FetchUsersControllerTest {
    @MockBean
    private lateinit var mockUserService: UserService

    @Mock
    private lateinit var testHttpServletRequest: HttpServletRequest

    private lateinit var testFetchUsersController: FetchUsersController

    private lateinit var pageRequest: PageRequest

    private lateinit var mockPage: Page<User>

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
    private val orderBy: String = "name"
    private val sortingDirection: String = "ASC"
    private val pageSize: Int = 10
    private val pageNumber: Int = 1
    private val filters: String = ""


    @BeforeEach
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        testFetchUsersController = FetchUsersController(mockUserService)

        `when`(testHttpServletRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value)).thenReturn(messageId)
        `when`(testHttpServletRequest.getHeader(ApiHeaderKey.TIMESTAMP.value)).thenReturn(OffsetDateTime.now().toString())

        pageRequest = TestHelper.createTestPageRequest(pageNumber, pageSize, sortingDirection, orderBy.split(","))
        mockPage = PageImpl<User>(listOf(mockUser), pageRequest, 1L)
    }

    @AfterEach
    fun tearDown(){
    }

    @Test
    fun testFetchUsers(){
        `when`(mockUserService.findUsers(MockitoHelper.anyObject(PageRequest::class.java), MockitoHelper.eq(filters)))
            .thenReturn(mockPage)
        val actualResponse: ResponseEntity<FetchUsersResponse> = testFetchUsersController
            .fetchUsers(orderBy, sortingDirection, pageSize.toString(), pageNumber.toString(), filters, testHttpServletRequest)

        Assert.assertEquals(HttpStatus.OK, actualResponse.statusCode)
        Assert.assertNotNull(actualResponse.body)
        val responseBody: FetchUsersResponse = actualResponse.body!!

        Assert.assertNotNull(responseBody.header.messageId)
        Assert.assertNotNull(responseBody.header.timestamp)

        Assert.assertEquals(1, responseBody.page)
        Assert.assertEquals(2, responseBody.totalPages)
        Assert.assertEquals(1, responseBody.numberOfUsers)

        val users: Array<UserDTO> = responseBody.users!!
        Assert.assertEquals(1, users.size)

        val userDetails: UserDTO = users[0]
        Assert.assertEquals(userId, userDetails.id)
        Assert.assertEquals(mockUser.name, userDetails.name)
        Assert.assertEquals(mockUser.city, userDetails.city)
        Assert.assertEquals(mockUser.dateOfBirth, userDetails.dateOfBirth)
        Assert.assertEquals(mockUser.email, userDetails.email)
        Assert.assertEquals(mockUser.phoneNumber, userDetails.phoneNumber)

        verify(mockUserService, Mockito.times(1))
            .findUsers(MockitoHelper.anyObject(PageRequest::class.java), MockitoHelper.eq(filters))
    }

    @Test
    fun testFetchUsers_FilterCriteriaWithEmptyResults_ReturnsSuccessResponse(){
        `when`(mockUserService.findUsers(MockitoHelper.anyObject(PageRequest::class.java), MockitoHelper.eq(filters)))
            .thenReturn(Page.empty(pageRequest))
        val actualResponse: ResponseEntity<FetchUsersResponse> = testFetchUsersController
            .fetchUsers(orderBy, sortingDirection, pageSize.toString(), pageNumber.toString(), filters, testHttpServletRequest)

        Assert.assertEquals(HttpStatus.OK, actualResponse.statusCode)
        Assert.assertNotNull(actualResponse.body)
        val responseBody: FetchUsersResponse = actualResponse.body!!

        Assert.assertNotNull(responseBody.header.messageId)
        Assert.assertNotNull(responseBody.header.timestamp)

        Assert.assertEquals(1, responseBody.page)
        Assert.assertEquals(0, responseBody.totalPages)
        Assert.assertEquals(0, responseBody.numberOfUsers)

        val users: Array<UserDTO> = responseBody.users!!
        Assert.assertEquals(0, users.size)

        verify(mockUserService, Mockito.times(1))
            .findUsers(MockitoHelper.anyObject(PageRequest::class.java), MockitoHelper.eq(filters))
    }

    @Test
    fun testFetchUsers_UnexpectedException_Propagated(){
        val unexpectedException: RuntimeException = RuntimeException("An unexpected error occurred")

        `when`(mockUserService.findUsers(MockitoHelper.anyObject(PageRequest::class.java), MockitoHelper.eq(filters)))
            .thenThrow(unexpectedException)

        val thrownException:RuntimeException = Assert.assertThrows(RuntimeException::class.java){
            testFetchUsersController.fetchUsers(
                orderBy,
                sortingDirection,
                pageSize.toString(),
                pageNumber.toString(),
                filters,
                testHttpServletRequest)
        }

        Assert.assertEquals(unexpectedException, thrownException)
        verify(mockUserService, Mockito.times(1))
            .findUsers(MockitoHelper.anyObject(PageRequest::class.java), MockitoHelper.eq(filters))
    }
}