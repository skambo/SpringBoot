package io.skambo.example.integration.v1

import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import io.skambo.example.infrastructure.api.common.dto.v1.UserDTO
import io.skambo.example.infrastructure.api.fetchusers.v1.dto.FetchUsersResponse
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.integration.BaseApiIntegrationTest
import io.skambo.example.integration.utils.TestScenario
import org.junit.Assert
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class FetchUsersApiIntegrationTest: BaseApiIntegrationTest<Unit, FetchUsersResponse>() {
    private final val name: String = "Anne"
    private final val dateOfBirth: OffsetDateTime = LocalDateTime
        .parse("2017-02-03 12:30:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        .atOffset(ZoneOffset.UTC)
    private final val city: String = "Nairobi"
    private final val email: String = "anne@gmail.com"
    private final val phoneNumber: String = "254711123123"

    override val endpoint: String = "/api/v1/fetchUsers"

    override val httpMethod: HttpMethod = HttpMethod.GET

    override val requestBody: Unit? = null

    private val userOne: UserDataModel = UserDataModel(
        name = name,
        dateOfBirth = dateOfBirth.toString(),
        city = city,
        email = email,
        phoneNumber = phoneNumber
    )

    private val userTwo: UserDataModel = UserDataModel(
        name = "Michael",
        dateOfBirth = dateOfBirth.toString(),
        city = "London",
        email = "michael@gmail.com",
        phoneNumber = "447911123456"
    )

    private val userThree: UserDataModel = UserDataModel(
        name = "Michael",
        dateOfBirth = dateOfBirth.toString(),
        city = "Nairobi",
        email = "michael1@gmail.com",
        phoneNumber = "254722123123"
    )

    private var userOneId: Long? = null

    private var userTwoId: Long? = null

    private var userThreeId: Long? = null


    override fun createTestScenarios(): List<TestScenario<Unit, FetchUsersResponse>> {
        createTestData()
        return listOf(
            successScenario(),
            noUsersFoundScenario(),
            filterByNameAndCityScenario(),
            filterByCity(),
            filterByPhoneNumber(),
            sortingDirectionScenario(),
            sortingFieldsScenario(),
            pageSizeScenario(),
            pageSizeAndNumberScenario(),
            filtersSortingAndPaginationScenario()
        )
    }

    private fun createTestData(){
        this.userRepository.saveAll(listOf(userOne, userTwo, userThree))
        Assert.assertEquals(3, this.userRepository.findAll().toList().size)

        userOneId = this.userRepository.findByEmail(userOne.email).get().id!!
        userTwoId = this.userRepository.findByEmail(userTwo.email).get().id!!
        userThreeId = this.userRepository.findByEmail(userThree.email).get().id!!
    }

    private fun successScenario(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Fetch users scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 1,
                numberOfUsers = 3,
                totalPages = 1,
                users = arrayOf(
                    UserDTO(
                        id = userOneId,
                        name = userOne.name,
                        dateOfBirth = dateOfBirth,
                        city = userOne.city,
                        email = userOne.email,
                        phoneNumber = userOne.phoneNumber
                    ),
                    UserDTO(
                        id = userTwoId,
                        name = userTwo.name,
                        dateOfBirth = dateOfBirth,
                        city = userTwo.city,
                        email = userTwo.email,
                        phoneNumber = userTwo.phoneNumber
                    ),
                    UserDTO(
                        id = userThreeId,
                        name = userThree.name,
                        dateOfBirth = dateOfBirth,
                        city = userThree.city,
                        email = userThree.email,
                        phoneNumber = userThree.phoneNumber
                    )
                )
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun noUsersFoundScenario(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "No users found scenario",
            endpoint = "${this.endpoint}?filters=name:nonexistent,city:nonexistent",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 1,
                numberOfUsers = 0,
                totalPages = 0,
                users = arrayOf()
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun filterByCity(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Filter by city scenario",
            endpoint = "${this.endpoint}?filters=city:${city}",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 1,
                numberOfUsers = 2,
                totalPages = 1,
                users = arrayOf(
                    UserDTO(
                        id = userOneId,
                        name = userOne.name,
                        dateOfBirth = dateOfBirth,
                        city = userOne.city,
                        email = userOne.email,
                        phoneNumber = userOne.phoneNumber
                    ),
                    UserDTO(
                        id = userThreeId,
                        name = userThree.name,
                        dateOfBirth = dateOfBirth,
                        city = userThree.city,
                        email = userThree.email,
                        phoneNumber = userThree.phoneNumber
                    )
                )
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    //TODO Fix the filter by email
//    private fun filterByEmail(): TestScenario<Unit, FetchUsersResponse> {
//        //This is a high order function
//        val preScenario: () -> Unit = {}
//
//        val postScenario: () -> Unit = {}
//
//        return TestScenario(
//            description = "Filter by email scenario",
//            endpoint = "${this.endpoint}?filters=email:${userOne.email}",
//            httpHeaders = this.httpHeaders,
//            requestBody = this.requestBody,
//            expectedHttpStatus = HttpStatus.OK,
//            expectedResponseBody = FetchUsersResponse(
//                header = Header(
//                    messageId = UUID.randomUUID().toString(),
//                    timestamp = OffsetDateTime.now(),
//                    responseStatus = Status(
//                        status = ResponseStatus.SUCCESS.value
//                    )
//                ),
//                page = 1,
//                numberOfUsers = 1,
//                totalPages = 1,
//                users = arrayOf(
//                    UserDTO(
//                        id = userOneId,
//                        name = userOne.name,
//                        dateOfBirth = dateOfBirth,
//                        city = userOne.city,
//                        email = userOne.email,
//                        phoneNumber = userOne.phoneNumber
//                    )
//                )
//            ),
//            responseClass = FetchUsersResponse::class.java,
//            preScenario = preScenario,
//            postScenario = postScenario
//        )
//    }

    private fun filterByPhoneNumber(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Filter by phoneNumber scenario",
            endpoint = "${this.endpoint}?filters=phoneNumber:${userOne.phoneNumber}",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 1,
                numberOfUsers = 1,
                totalPages = 1,
                users = arrayOf(
                    UserDTO(
                        id = userOneId,
                        name = userOne.name,
                        dateOfBirth = dateOfBirth,
                        city = userOne.city,
                        email = userOne.email,
                        phoneNumber = userOne.phoneNumber
                    )
                )
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun filterByNameAndCityScenario(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Filter by name and city scenario",
            endpoint = "${this.endpoint}?filters=name:${userOne.name},city:${userOne.city}",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 1,
                numberOfUsers = 1,
                totalPages = 1,
                users = arrayOf(
                    UserDTO(
                        id = userOneId,
                        name = userOne.name,
                        dateOfBirth = dateOfBirth,
                        city = userOne.city,
                        email = userOne.email,
                        phoneNumber = userOne.phoneNumber
                    )
                )
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun sortingDirectionScenario(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Sorting direction scenario",
            endpoint = "${this.endpoint}?sortingDirection=desc",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 1,
                numberOfUsers = 3,
                totalPages = 1,
                users = arrayOf(
                    UserDTO(
                        id = userTwoId,
                        name = userTwo.name,
                        dateOfBirth = dateOfBirth,
                        city = userTwo.city,
                        email = userTwo.email,
                        phoneNumber = userTwo.phoneNumber
                    ),
                    UserDTO(
                        id = userThreeId,
                        name = userThree.name,
                        dateOfBirth = dateOfBirth,
                        city = userThree.city,
                        email = userThree.email,
                        phoneNumber = userThree.phoneNumber
                    ),
                    UserDTO(
                        id = userOneId,
                        name = userOne.name,
                        dateOfBirth = dateOfBirth,
                        city = userOne.city,
                        email = userOne.email,
                        phoneNumber = userOne.phoneNumber
                    )
                )
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun sortingFieldsScenario(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Sorting fields scenario",
            endpoint = "${this.endpoint}?sortingDirection=desc&orderBy=city,name",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 1,
                numberOfUsers = 3,
                totalPages = 1,
                users = arrayOf(
                    UserDTO(
                        id = userThreeId,
                        name = userThree.name,
                        dateOfBirth = dateOfBirth,
                        city = userThree.city,
                        email = userThree.email,
                        phoneNumber = userThree.phoneNumber
                    ),
                    UserDTO(
                        id = userOneId,
                        name = userOne.name,
                        dateOfBirth = dateOfBirth,
                        city = userOne.city,
                        email = userOne.email,
                        phoneNumber = userOne.phoneNumber
                    ),
                    UserDTO(
                        id = userTwoId,
                        name = userTwo.name,
                        dateOfBirth = dateOfBirth,
                        city = userTwo.city,
                        email = userTwo.email,
                        phoneNumber = userTwo.phoneNumber
                    )
                )
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun pageSizeScenario(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Page size scenario",
            endpoint = "${this.endpoint}?pageSize=1",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 1,
                numberOfUsers = 1,
                totalPages = 3,
                users = arrayOf(
                    UserDTO(
                        id = userOneId,
                        name = userOne.name,
                        dateOfBirth = dateOfBirth,
                        city = userOne.city,
                        email = userOne.email,
                        phoneNumber = userOne.phoneNumber
                    )
                )
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun pageSizeAndNumberScenario(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Page size and number scenario",
            endpoint = "${this.endpoint}?pageSize=1&pageNumber=3",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 3,
                numberOfUsers = 1,
                totalPages = 3,
                users = arrayOf(
                    UserDTO(
                        id = userThreeId,
                        name = userThree.name,
                        dateOfBirth = dateOfBirth,
                        city = userThree.city,
                        email = userThree.email,
                        phoneNumber = userThree.phoneNumber
                    )
                )
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun filtersSortingAndPaginationScenario(): TestScenario<Unit, FetchUsersResponse> {
        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Filters, sorting and pagination scenario",
            endpoint = "${this.endpoint}?filters=name:Michael&orderBy=name,city&sortingDirection=desc&pageSize=1&pageNumber=2",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUsersResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                page = 2,
                numberOfUsers = 1,
                totalPages = 2,
                users = arrayOf(
                    UserDTO(
                        id = userTwoId,
                        name = userTwo.name,
                        dateOfBirth = dateOfBirth,
                        city = userTwo.city,
                        email = userTwo.email,
                        phoneNumber = userTwo.phoneNumber
                    )
                )
            ),
            responseClass = FetchUsersResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }
}