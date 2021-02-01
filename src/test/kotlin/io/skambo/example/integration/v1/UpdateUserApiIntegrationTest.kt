package io.skambo.example.integration.v1

import io.skambo.example.ApiTestHelper
import io.skambo.example.TestHelper
import io.skambo.example.infrastructure.api.common.ErrorCodes
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.api.updateuser.v1.dto.UpdateUserResponse
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

class UpdateUserApiIntegrationTest: BaseApiIntegrationTest<String, UpdateUserResponse>() {
    private final val header: Header = ApiTestHelper.createTestHeader()
    private final val name: String = "Anne"
    private final val dateOfBirth: OffsetDateTime = LocalDateTime
        .parse("2017-02-03 12:30:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        .atOffset(ZoneOffset.UTC)
    private final val city: String = "Nairobi"

    override val endpoint: String = "/api/v1/updateUser"

    override val httpMethod: HttpMethod = HttpMethod.PATCH

    override val requestBody: String = TestHelper.convertToJsonString(
        mapOf(
            "header" to header,
            "name" to name,
            "dateOfBirth" to dateOfBirth,
            "city" to city
        )
    )

    override fun createTestScenarios(): List<TestScenario<String, UpdateUserResponse>> {
        return listOf(
            successScenario(),
            partialNameUpdateScenario(),
            partialDateOfBirthUpdateScenario(),
            partialCityUpdateScenario(),
            emailAndPhoneNumberUpdatesNotEffectedScenario(),
            userNotFoundScenario(),
            invalidUserIdScenario()
        )
    }

    private fun successScenario(): TestScenario<String, UpdateUserResponse>{
        val user: UserDataModel = UserDataModel(
            name = "Susan",
            dateOfBirth = "1950-01-01T00:33:20Z",
            city = "Kisumu",
            email = "susan@gmail.com",
            phoneNumber = "254722111111"
        )

        val userId: Long = this.userRepository.save(user).id!!

        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertTrue(this.userRepository.findById(userId).isPresent)
        }

        val postScenario: () -> Unit = {
            val optionalUser: Optional<UserDataModel> = this.userRepository.findById(userId)
            Assert.assertTrue(optionalUser.isPresent)

            val persistedUser: UserDataModel = optionalUser.get()
            Assert.assertEquals(name, persistedUser.name)
            Assert.assertEquals(dateOfBirth.toString(), persistedUser.dateOfBirth)
            Assert.assertEquals(city, persistedUser.city)
            Assert.assertEquals(user.email, persistedUser.email)
            Assert.assertEquals(user.phoneNumber, persistedUser.phoneNumber)
        }

        return TestScenario(
            description = "Update user details scenario",
            endpoint = "${this.endpoint}/$userId",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = UpdateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                id = userId,
                name = name,
                dateOfBirth = dateOfBirth,
                city = city,
                email = user.email,
                phoneNumber = user.phoneNumber
            ),
            responseClass = UpdateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun partialNameUpdateScenario(): TestScenario<String, UpdateUserResponse>{
        val user: UserDataModel = UserDataModel(
            name = "Bob",
            dateOfBirth = "1960-01-01T00:33:20Z",
            city = "Machakos",
            email = "bob@gmail.com",
            phoneNumber = "254722333333"
        )

        val userId: Long = this.userRepository.save(user).id!!
        val requestBody: String = TestHelper.convertToJsonString(
            mapOf(
                "header" to header,
                "name" to name
            )
        )

        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertTrue(this.userRepository.findById(userId).isPresent)
        }

        val postScenario: () -> Unit = {
            val optionalUser: Optional<UserDataModel> = this.userRepository.findById(userId)
            Assert.assertTrue(optionalUser.isPresent)

            val updatedUserDetails: UserDataModel = optionalUser.get()
            Assert.assertEquals(name, updatedUserDetails.name)
            Assert.assertEquals(user.dateOfBirth, updatedUserDetails.dateOfBirth)
            Assert.assertEquals(user.city, updatedUserDetails.city)
            Assert.assertEquals(user.email, updatedUserDetails.email)
            Assert.assertEquals(user.phoneNumber, updatedUserDetails.phoneNumber)
        }

        return TestScenario(
            description = "Update name only scenario",
            endpoint = "${this.endpoint}/$userId",
            httpHeaders = this.httpHeaders,
            requestBody = requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = UpdateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                id = userId,
                name = name,
                dateOfBirth = OffsetDateTime.parse(user.dateOfBirth),
                city = user.city,
                email = user.email,
                phoneNumber = user.phoneNumber
            ),
            responseClass = UpdateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun partialDateOfBirthUpdateScenario(): TestScenario<String, UpdateUserResponse>{
        val user: UserDataModel = UserDataModel(
            name = "John",
            dateOfBirth = "1960-01-01T00:33:20Z",
            city = "Machakos",
            email = "john@gmail.com",
            phoneNumber = "254722333444"
        )

        val userId: Long = this.userRepository.save(user).id!!
        val requestBody: String = TestHelper.convertToJsonString(
            mapOf(
                "header" to header,
                "dateOfBirth" to dateOfBirth
            )
        )

        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertTrue(this.userRepository.findById(userId).isPresent)
        }

        val postScenario: () -> Unit = {
            val optionalUser: Optional<UserDataModel> = this.userRepository.findById(userId)
            Assert.assertTrue(optionalUser.isPresent)

            val updatedUserDetails: UserDataModel = optionalUser.get()
            Assert.assertEquals(user.name, updatedUserDetails.name)
            Assert.assertEquals(dateOfBirth.toString(), updatedUserDetails.dateOfBirth)
            Assert.assertEquals(user.city, updatedUserDetails.city)
            Assert.assertEquals(user.email, updatedUserDetails.email)
            Assert.assertEquals(user.phoneNumber, updatedUserDetails.phoneNumber)
        }

        return TestScenario(
            description = "Update date of birth only scenario",
            endpoint = "${this.endpoint}/$userId",
            httpHeaders = this.httpHeaders,
            requestBody = requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = UpdateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                id = userId,
                name = user.name,
                dateOfBirth = dateOfBirth,
                city = user.city,
                email = user.email,
                phoneNumber = user.phoneNumber
            ),
            responseClass = UpdateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun partialCityUpdateScenario(): TestScenario<String, UpdateUserResponse>{
        val user: UserDataModel = UserDataModel(
            name = "Andrew",
            dateOfBirth = "1960-01-01T00:33:20Z",
            city = "Machakos",
            email = "andrew@gmail.com",
            phoneNumber = "254722333555"
        )

        val userId: Long = this.userRepository.save(user).id!!
        val requestBody: String = TestHelper.convertToJsonString(
            mapOf(
                "header" to header,
                "city" to city
            )
        )

        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertTrue(this.userRepository.findById(userId).isPresent)
        }

        val postScenario: () -> Unit = {
            val optionalUser: Optional<UserDataModel> = this.userRepository.findById(userId)
            Assert.assertTrue(optionalUser.isPresent)

            val updatedUserDetails: UserDataModel = optionalUser.get()
            Assert.assertEquals(user.name, updatedUserDetails.name)
            Assert.assertEquals(user.dateOfBirth, updatedUserDetails.dateOfBirth)
            Assert.assertEquals(city, updatedUserDetails.city)
            Assert.assertEquals(user.email, updatedUserDetails.email)
            Assert.assertEquals(user.phoneNumber, updatedUserDetails.phoneNumber)
        }

        return TestScenario(
            description = "Update city only scenario",
            endpoint = "${this.endpoint}/$userId",
            httpHeaders = this.httpHeaders,
            requestBody = requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = UpdateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                id = userId,
                name = user.name,
                dateOfBirth = OffsetDateTime.parse(user.dateOfBirth),
                city = city,
                email = user.email,
                phoneNumber = user.phoneNumber
            ),
            responseClass = UpdateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun emailAndPhoneNumberUpdatesNotEffectedScenario(): TestScenario<String, UpdateUserResponse>{
        val user: UserDataModel = UserDataModel(
            name = "Job",
            dateOfBirth = "1960-01-01T00:33:20Z",
            city = "Machakos",
            email = "job@gmail.com",
            phoneNumber = "254722333666"
        )

        val userId: Long = this.userRepository.save(user).id!!
        val requestBody: String = TestHelper.convertToJsonString(
            mapOf(
                "header" to header,
                "email" to "job2@gmail.com",
                "phoneNumber" to "254721123123"
            )
        )

        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertTrue(this.userRepository.findById(userId).isPresent)
        }

        val postScenario: () -> Unit = {
            val optionalUser: Optional<UserDataModel> = this.userRepository.findById(userId)
            Assert.assertTrue(optionalUser.isPresent)

            val persistedUserDetails: UserDataModel = optionalUser.get()
            Assert.assertEquals(user.name, persistedUserDetails.name)
            Assert.assertEquals(user.dateOfBirth, persistedUserDetails.dateOfBirth)
            Assert.assertEquals(user.city, persistedUserDetails.city)
            Assert.assertEquals(user.email, persistedUserDetails.email)
            Assert.assertEquals(user.phoneNumber, persistedUserDetails.phoneNumber)
        }

        return TestScenario(
            description = "Ensure email and phone number updates aren't effected scenario",
            endpoint = "${this.endpoint}/$userId",
            httpHeaders = this.httpHeaders,
            requestBody = requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = UpdateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                id = userId,
                name = user.name,
                dateOfBirth = OffsetDateTime.parse(user.dateOfBirth),
                city = user.city,
                email = user.email,
                phoneNumber = user.phoneNumber
            ),
            responseClass = UpdateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun userNotFoundScenario(): TestScenario<String, UpdateUserResponse>{
        val userId: Long = 1000L

        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findById(userId).isPresent)
        }

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "User not found scenario",
            endpoint = "${this.endpoint}/$userId",
            httpHeaders = this.httpHeaders,
            requestBody = requestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = UpdateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.USER_NOT_FOUND_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.USER_NOT_FOUND_ERR.value)
                    )
                )
            ),
            responseClass = UpdateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun invalidUserIdScenario(): TestScenario<String, UpdateUserResponse>{
        val userId: String = "ten"

        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Invalid userId scenario",
            endpoint = "${this.endpoint}/$userId",
            httpHeaders = this.httpHeaders,
            requestBody = requestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = UpdateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.USER_NOT_FOUND_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.USER_NOT_FOUND_ERR.value)
                    )
                )
            ),
            responseClass = UpdateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }
}