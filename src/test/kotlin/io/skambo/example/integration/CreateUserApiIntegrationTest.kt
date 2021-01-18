package io.skambo.example.integration

import io.skambo.example.infrastructure.api.ApiTestHelper
import io.skambo.example.infrastructure.api.createuser.v1.CreateUserController
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserRequest
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.integration.utils.TestScenario
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.OffsetDateTime

class CreateUserApiIntegrationTest: ApiBaseIntegrationTest<CreateUserRequest, CreateUserResponse>() {
    @Test
    fun createUserIntegrationTest(){
        val testScenarios: List<TestScenario<CreateUserRequest, CreateUserResponse>> = listOf(
            TestScenario(
                url = "api/v1/createUser",
                httpMethod = HttpMethod.POST,
                httpHeaders = this.httpHeaders,
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
        this.runTestScenarios(testScenarios)
    }
}