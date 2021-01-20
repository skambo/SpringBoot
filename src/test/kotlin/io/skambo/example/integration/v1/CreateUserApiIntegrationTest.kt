package io.skambo.example.integration.v1

import io.skambo.example.ApiTestHelper
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserRequest
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.integration.BaseApiIntegrationTest
import io.skambo.example.integration.utils.TestScenario
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
         return listOf(
            TestScenario(
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
                responseClass = CreateUserResponse::class.java
            )
        )
    }
}