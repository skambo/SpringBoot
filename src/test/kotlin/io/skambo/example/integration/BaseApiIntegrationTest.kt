package io.skambo.example.integration

import io.skambo.example.ApiTestHelper
import io.skambo.example.TestHelper
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
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
import net.javacrumbs.jsonunit.core.Option
import org.junit.Assert
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.AssertionError
import java.time.OffsetDateTime
import java.util.*

@ActiveProfiles("memory-test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseApiIntegrationTest<RequestType, ResponseType> {
    protected abstract val endpoint: String

    protected abstract val httpMethod: HttpMethod

    protected abstract val requestBody: RequestType?

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

    private val logger: Logger = LoggerFactory.getLogger(BaseApiIntegrationTest::class.java)

    protected abstract fun createTestScenarios(): List<TestScenario<RequestType, ResponseType>>

    @BeforeEach
    fun setUp(){
        this.httpHeaders = ApiTestHelper.createHttpHeaders()
        this.httpHeaders.add("Authorization", testAuthorizationToken)
    }

    @Test
    fun runIntegrationTests(){
        this.runTestScenarios(this.createTestScenarios())
//        TODO This is a temporary solution because the clearDatabaseRule() does not seem to work
        this.userRepository.deleteAll()
    }

    @Test
    fun testMissingAuthorizationHeader(){
        val headers: HttpHeaders = ApiTestHelper.createHttpHeaders()

        val testScenarios: List<TestScenario<RequestType, ApiErrorResponse>> = listOf(
            TestScenario(
                description = "Missing authorization header scenario",
                endpoint = this.endpoint,
                httpHeaders = headers,
                requestBody = this.requestBody,
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

    @Test
    fun testInvalidAuthorizationToken(){
        val headers: HttpHeaders = ApiTestHelper.createHttpHeaders()

        headers.add("Authorization", "wrongApiKey$testAuthorizationToken")

        val testScenarios: List<TestScenario<RequestType, ApiErrorResponse>> = listOf(
            TestScenario(
                description = "Invalid authorization header scenario",
                endpoint = this.endpoint,
                httpHeaders = headers,
                requestBody = this.requestBody,
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

    @Test
    fun testMissingMessageId(){
        val headers: HttpHeaders = ApiTestHelper.createHttpHeaders()

        headers.add("Authorization", testAuthorizationToken)
        headers.remove(ApiHeaderKey.MESSAGE_ID.value)

        val testScenarios: List<TestScenario<RequestType, ApiErrorResponse>> = listOf(
            TestScenario(
                description = "Missing messageId header scenario",
                endpoint = this.endpoint,
                httpHeaders = headers,
                requestBody = this.requestBody,
                expectedHttpStatus = HttpStatus.BAD_REQUEST,
                expectedResponseBody = ApiErrorResponse(
                    header = Header(
                        messageId = UUID.randomUUID().toString(),
                        timestamp = OffsetDateTime.now(),
                        responseStatus = Status(
                            status = ResponseStatus.REJECTED.value,
                            errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                            errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.MISSING_MESSAGE_ID_HEADER_ERR_MSG.value)
                        )
                    )
                ),
                responseClass = ApiErrorResponse::class.java
            )
        )
        runTestScenarios(testScenarios)
    }

    @Test
    fun testMissingTimestampHeader(){
        val headers: HttpHeaders = ApiTestHelper.createHttpHeaders()

        headers.add("Authorization", testAuthorizationToken)
        headers.remove(ApiHeaderKey.TIMESTAMP.value)

        val testScenarios: List<TestScenario<RequestType, ApiErrorResponse>> = listOf(
            TestScenario(
                description = "Missing timestamp header scenario",
                endpoint = this.endpoint,
                httpHeaders = headers,
                requestBody = this.requestBody,
                expectedHttpStatus = HttpStatus.BAD_REQUEST,
                expectedResponseBody = ApiErrorResponse(
                    header = Header(
                        messageId = UUID.randomUUID().toString(),
                        timestamp = OffsetDateTime.now(),
                        responseStatus = Status(
                            status = ResponseStatus.REJECTED.value,
                            errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                            errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.MISSING_TIMESTAMP_HEADER_ERR_MSG.value)
                        )
                    )
                ),
                responseClass = ApiErrorResponse::class.java
            )
        )
        runTestScenarios(testScenarios)
    }

    @Test
    fun testInvalidTimestamp(){
        val invalidTimestamp: String = "noTimestamp"
        val headers: HttpHeaders = ApiTestHelper.createHttpHeaders()

        headers.add("Authorization", testAuthorizationToken)
        headers.set(ApiHeaderKey.TIMESTAMP.value, invalidTimestamp)

        val testScenarios: List<TestScenario<RequestType, ApiErrorResponse>> = listOf(
            TestScenario(
                description = "Invalid timestamp header scenario",
                endpoint = this.endpoint,
                httpHeaders = headers,
                requestBody = this.requestBody,
                expectedHttpStatus = HttpStatus.BAD_REQUEST,
                expectedResponseBody = ApiErrorResponse(
                    header = Header(
                        messageId = UUID.randomUUID().toString(),
                        timestamp = OffsetDateTime.now(),
                        responseStatus = Status(
                            status = ResponseStatus.REJECTED.value,
                            errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                            errorMessage = ApiResponseHelper.lookupErrorMessage(
                                ErrorCodes.INVALID_TIMESTAMP_ERR_MSG.value,
                                invalidTimestamp
                            )
                        )
                    )
                ),
                responseClass = ApiErrorResponse::class.java
            )
        )
        runTestScenarios(testScenarios)
    }

    private fun <RequestType, ResponseType> runTestScenarios(
        testScenarios: List<TestScenario<RequestType, ResponseType>>
    ){
        for(scenario: TestScenario<RequestType, ResponseType> in testScenarios){
            logger.info("\nExecuting ${scenario.description}")

            //This is the pre scenario higher order function that is called before the scenario is executed
            //It is ideal for setting up data required for the test scenario
            scenario.preScenario()

            val responseEntity: ResponseEntity<ResponseType> = this.testRestTemplate.exchange(
                "http://localhost:$port${scenario.endpoint}",
                this.httpMethod,
                HttpEntity(scenario.requestBody, scenario.httpHeaders),
                scenario.responseClass
            )
            assertResponse(responseEntity, scenario.expectedHttpStatus, scenario.expectedResponseBody)
            //This is the post scenario higher order function that is called after the scenario is executed
            //It is ideal for cleaning up data or adding extra assertions
            scenario.postScenario()
        }
    }

    private fun <ResponseType> assertResponse(
        responseEntity: ResponseEntity<ResponseType>,
        expectedHttpStatus: HttpStatus,
        expectedResponseBody: ResponseType
    ) {
        Assert.assertEquals(expectedHttpStatus, responseEntity.statusCode)
        assertResponseObject(expectedResponseBody, responseEntity.body)
    }

    private fun <ResponseType> assertResponseObject(
        expected: ResponseType,
        actual: ResponseType,
        description: String = ""
    ) {
        try{
            assertThatJson(TestHelper.convertToJsonString(actual))
                .whenIgnoringPaths("header.timestamp", "header.messageId", "header.groupId")
                .`when`(Option.IGNORING_EXTRA_FIELDS)
                .describedAs(description)
                .isEqualTo(TestHelper.convertToJsonString(expected))
        } catch(throwable:Throwable){
            throw AssertionError(throwable.localizedMessage, throwable)
        }
    }
}