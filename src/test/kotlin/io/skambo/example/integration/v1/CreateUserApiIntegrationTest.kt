package io.skambo.example.integration.v1

import io.skambo.example.ApiTestHelper
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserRequest
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
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

class CreateUserApiIntegrationTest: BaseApiIntegrationTest<CreateUserRequest, CreateUserResponse>() {

    override val url: String = "/api/v1/createUser"

    override val httpMethod: HttpMethod = HttpMethod.POST

    override val requestBody: CreateUserRequest = CreateUserRequest(
        header = ApiTestHelper.createTestHeader(),
        name = "Anne",
        dateOfBirth = LocalDateTime.parse(
            "2017-02-03 12:30:30",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        ).atOffset(ZoneOffset.UTC),
        city = "Nairobi",
        email = "anne@gmail.com",
        phoneNumber = "1224"
    )

    override fun createTestScenarios(): List<TestScenario<CreateUserRequest, CreateUserResponse>> {
         return listOf(createUserSuccessScenario())
    }

    private fun createUserSuccessScenario(): TestScenario<CreateUserRequest, CreateUserResponse>{
        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(this.requestBody.email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(this.requestBody.phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            val optionalUser: Optional<UserDataModel> = this.userRepository.findByEmail(this.requestBody.email)
            Assert.assertTrue(optionalUser.isPresent)

            val persistedUser: UserDataModel = optionalUser.get()
            Assert.assertEquals(this.requestBody.name, persistedUser.name)
            Assert.assertEquals(this.requestBody.dateOfBirth.toString(), persistedUser.dateOfBirth)
            Assert.assertEquals(this.requestBody.city, persistedUser.city)
            Assert.assertEquals(this.requestBody.email, persistedUser.email)
            Assert.assertEquals(this.requestBody.phoneNumber, persistedUser.phoneNumber)

            //We're cleaning up the created user
            this.userRepository.delete(persistedUser)
        }

        return TestScenario(
            description = "Create user success scenario",
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.CREATED,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                id = 1L,
                name = this.requestBody.name,
                dateOfBirth = this.requestBody.dateOfBirth,
                city = this.requestBody.city,
                email = this.requestBody.email,
                phoneNumber = this.requestBody.phoneNumber
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }
}