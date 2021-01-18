package io.skambo.example.integration

import io.skambo.example.infrastructure.api.ApiTestHelper
import io.skambo.example.infrastructure.api.createuser.v1.CreateUserController
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserRequest
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.integration.utils.TestScenario
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime

class CreateUserApiIntegrationTest: ApiBaseIntegrationTest<CreateUserRequest, CreateUserResponse>() {

    override val url: String = "/api/v1/createUser"

    override val httpMethod: HttpMethod = HttpMethod.POST

    override val requestBody: CreateUserRequest = CreateUserRequest(
        header = ApiTestHelper.createTestHeader(),
        name = "Anne",
        dateOfBirth = OffsetDateTime.now(),
        city = "Nairobi",
        email = "anne@gmail.com",
        phoneNumber = "1224"
    )

    override fun createTestScenarios(): List<TestScenario<CreateUserRequest, CreateUserResponse>> {
         return listOf(
            TestScenario(
                httpHeaders = this.httpHeaders,
                requestBody = this.requestBody,
                expectedHttpStatus = HttpStatus.CREATED,
                expectedResponseBody = CreateUserResponse(
                    header = ApiTestHelper.createTestHeader(),
                    id = 1L,
                    name = "Anne",
                    dateOfBirth = OffsetDateTime.now(),
                    city = "Nairobi",
                    email = "anne@gmail.com",
                    phoneNumber = "1224"
                ),
                responseClass = CreateUserResponse::class.java
            )
        )
    }
}