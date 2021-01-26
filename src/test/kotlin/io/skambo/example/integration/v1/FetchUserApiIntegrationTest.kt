package io.skambo.example.integration.v1

import io.skambo.example.infrastructure.api.common.ErrorCodes
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.api.fetchuser.v1.dto.FetchUserResponse
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

class FetchUserApiIntegrationTest: BaseApiIntegrationTest<Unit, FetchUserResponse>() {
    private final val name: String = "Anne"
    private final val dateOfBirth: OffsetDateTime = LocalDateTime
        .parse("2017-02-03 12:30:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        .atOffset(ZoneOffset.UTC)
    private final val city: String = "Nairobi"
    private final val email: String = "anne@gmail.com"
    private final val phoneNumber: String = "1224"

    override val endpoint: String = "/api/v1/fetchUser/"

    override val httpMethod: HttpMethod = HttpMethod.GET

    override val requestBody: Unit? = null

    override fun createTestScenarios(): List<TestScenario<Unit, FetchUserResponse>> {
        return listOf(successScenario(), userNotFoundScenario(), invalidUserIdScenario())
    }

    private fun successScenario(): TestScenario<Unit, FetchUserResponse>{
        val createdUser: UserDataModel = UserDataModel(
            name = name,
            dateOfBirth = dateOfBirth.toString(),
            city = city,
            email = email,
            phoneNumber = phoneNumber
        )

        this.userRepository.save(createdUser)

        val userId: Long = this.userRepository.findByEmail(email).get().id!!

        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertTrue(this.userRepository.findById(userId).isPresent)
        }

        val postScenario: () -> Unit = {
            val optionalUser: Optional<UserDataModel> = this.userRepository.findById(userId)
            Assert.assertTrue(optionalUser.isPresent)
        }

        return TestScenario(
            description = "Fetch user scenario",
            endpoint = "${this.endpoint}$userId",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.OK,
            expectedResponseBody = FetchUserResponse(
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
                email = email,
                phoneNumber = phoneNumber
            ),
            responseClass = FetchUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun userNotFoundScenario(): TestScenario<Unit, FetchUserResponse>{
        val userId: Long = 1000L

        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findById(userId).isPresent)
        }

        val postScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findById(userId).isPresent)
        }

        return TestScenario(
            description = "User not found scenario",
            endpoint = "${this.endpoint}$userId",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = FetchUserResponse(
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
            responseClass = FetchUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun invalidUserIdScenario(): TestScenario<Unit, FetchUserResponse>{
        val userId: String = "Ten"

        //This is a high order function
        val preScenario: () -> Unit = {}

        val postScenario: () -> Unit = {}

        return TestScenario(
            description = "Invalid userId scenario",
            endpoint = "${this.endpoint}$userId",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = FetchUserResponse(
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
            responseClass = FetchUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }
}