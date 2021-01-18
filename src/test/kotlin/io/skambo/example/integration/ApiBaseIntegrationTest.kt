package io.skambo.example.integration

import io.skambo.example.infrastructure.api.ApiTestHelper
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.common.dto.v1.ApiErrorResponse
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import io.skambo.example.integration.rules.ClearDatabaseRule
import io.skambo.example.integration.utils.TestScenario
import org.json.JSONObject
import org.junit.Assert
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.util.*

@ActiveProfiles("memory-test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ApiBaseIntegrationTest<ClassRequestType, ClassResponseType> {
    protected abstract val url: String

    protected abstract val httpMethod: HttpMethod

    protected abstract val requestBody: ClassRequestType?

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    @Rule
    lateinit var clearDatabaseRule: ClearDatabaseRule

    @Autowired
    protected lateinit var testRestTemplate: TestRestTemplate

    @LocalServerPort
    protected var port: Int = 0

    @Value("#{'\${http.auth-token}'.split(',')[0]}")
    protected val testAuthorizationToken: String? = null

    protected val httpHeaders: HttpHeaders = HttpHeaders()

    protected abstract fun createTestScenarios(): List<TestScenario<ClassRequestType, ClassResponseType>>

    @BeforeEach
    fun setUp(){
        this.httpHeaders.add(ApiHeaderKey.MESSAGE_ID.value, UUID.randomUUID().toString())
        this.httpHeaders.add(ApiHeaderKey.TIMESTAMP.value, OffsetDateTime.now().toString())
        this.httpHeaders.add("Authorization", testAuthorizationToken)
    }

    @Test
    fun runIntegrationTests(){
        this.runTestScenarios(this.createTestScenarios())
    }

    @Test
    fun testInvalidApiKey(){
        val headers: HttpHeaders = HttpHeaders()

        headers.add(ApiHeaderKey.MESSAGE_ID.value, UUID.randomUUID().toString())
        headers.add(ApiHeaderKey.TIMESTAMP.value, OffsetDateTime.now().toString())

        val testScenarios: List<TestScenario<ClassRequestType, ApiErrorResponse>> = listOf(
            TestScenario(
                httpHeaders = headers,
                requestBody = this.requestBody!!,
                expectedHttpStatus = HttpStatus.UNAUTHORIZED,
                expectedResponseBody = ApiErrorResponse(header = ApiTestHelper.createTestHeader()),
                responseClass = ApiErrorResponse::class.java
            )
        )
        runTestScenarios(testScenarios)
    }

    private fun <RequestClass, ResponseClass> runTestScenarios(
        testScenarios: List<TestScenario<RequestClass, ResponseClass>>
    ){
        for(scenario: TestScenario<RequestClass, ResponseClass> in testScenarios){
            val responseEntity: ResponseEntity<ResponseClass> = this.testRestTemplate.exchange(
                "http://localhost:$port${this.url}",
                this.httpMethod,
                HttpEntity(scenario.requestBody, scenario.httpHeaders),
                scenario.responseClass
            )
            assertResponse(responseEntity, scenario.expectedHttpStatus, scenario.expectedResponseBody)
        }
    }

    private fun <ResponseClass> assertResponse(
        responseEntity: ResponseEntity<ResponseClass>,
        expectedHttpStatus: HttpStatus,
        expectedResponseBody: ResponseClass
    ) {
        Assert.assertEquals(expectedHttpStatus, responseEntity.statusCode)
//      Assertions.assertThat(responseEntity.body).usingRecursiveComparison().ignoringFields("header")
//            .isEqualTo(expectedResponseBody)
    }
}