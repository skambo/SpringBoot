package io.skambo.example.integration

import io.skambo.example.infrastructure.api.ApiTestHelper
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserRequest
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.integration.utils.TestScenario
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime


class CreateUserIntegrationTestApi: ApiBaseIntegrationTest<CreateUserRequest, CreateUserResponse>() {
    override fun createTestScenarios(): List<TestScenario<CreateUserRequest, CreateUserResponse>> {
        return listOf(
            TestScenario(
                url = "/v1/createUser",
                httpMethod = HttpMethod.POST,
                httpHeaders = HttpHeaders(),
                requestBody = CreateUserRequest(
                    header = ApiTestHelper.createTestHeader(),
                    name = "Anne",
                    dateOfBirth = OffsetDateTime.now(),
                    city = "Nairobi",
                    email = "anne@gmail.com",
                    phoneNumber = "1224"
                ),
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