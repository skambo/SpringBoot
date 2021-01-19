package io.skambo.example.integration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.skambo.example.ApiTestHelper
import io.skambo.example.infrastructure.api.common.ErrorCodes
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.ApiErrorResponse
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import io.skambo.example.integration.rules.ClearDatabaseRule
import io.skambo.example.integration.utils.TestScenario
import net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson
import org.assertj.core.api.Assertions
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
abstract class BaseApiIntegrationTest<ClassRequestType, ClassResponseType> {
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

    protected lateinit var httpHeaders: HttpHeaders

    protected abstract fun createTestScenarios(): List<TestScenario<ClassRequestType, ClassResponseType>>

    @BeforeEach
    fun setUp(){
        this.httpHeaders = ApiTestHelper.createHttpHeaders()
        this.httpHeaders.add("Authorization", testAuthorizationToken)
    }

    @Test
    fun runIntegrationTests(){
        this.runTestScenarios(this.createTestScenarios())
    }

    @Test
    fun testMissingAuthorizationHeader(){
        val headers: HttpHeaders = ApiTestHelper.createHttpHeaders()

        val testScenarios: List<TestScenario<ClassRequestType, ApiErrorResponse>> = listOf(
            TestScenario(
                httpHeaders = headers,
                requestBody = this.requestBody!!,
                expectedHttpStatus = HttpStatus.UNAUTHORIZED,
                expectedResponseBody = ApiErrorResponse(
                    header = Header(
                        messageId = UUID.randomUUID().toString(),
                        timestamp = OffsetDateTime.now(),
                        responseStatus = Status(
                            status = ResponseStatus.REJECTED.value,
                            errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_API_KEY_ERR.value),
                            errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.INVALID_API_KEY_ERR.value)
                        )
                    )
                ),
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
        comparingResponseObject(expectedResponseBody, responseEntity.body)
    }

    @Throws(JsonProcessingException::class)
    private fun <ResponseClass> comparingResponseObject(
        expected: ResponseClass,
        actual: ResponseClass,
        description: String = ""
    ) {
        val objectMapper = ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        assertThatJson(objectMapper.writeValueAsString(actual))
            .whenIgnoringPaths("header.timestamp", "header.messageId", "header.groupId")
            .describedAs(description)
            .isEqualTo(
                objectMapper.writeValueAsString(
                    expected
                )
            )
    }
}