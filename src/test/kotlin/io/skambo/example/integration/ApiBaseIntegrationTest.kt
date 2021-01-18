package io.skambo.example.integration

import io.skambo.example.SpringExampleApplication
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import io.skambo.example.integration.rules.ClearDatabaseRule
import io.skambo.example.integration.utils.TestScenario
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner
import java.time.OffsetDateTime
import java.util.*

@ActiveProfiles("memory-test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ApiBaseIntegrationTest<RequestType, ResponseType> {
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

    @BeforeEach
    fun setUp(){
        this.httpHeaders.add(ApiHeaderKey.MESSAGE_ID.value, UUID.randomUUID().toString())
        this.httpHeaders.add(ApiHeaderKey.TIMESTAMP.value, OffsetDateTime.now().toString())
        this.httpHeaders.add("Authorization", testAuthorizationToken)
    }

    protected fun runTestScenarios(
        testScenarios: List<TestScenario<RequestType, ResponseType>>
    ){
        for(scenario: TestScenario<RequestType, ResponseType> in testScenarios){
            val responseEntity: ResponseEntity<ResponseType> = this.testRestTemplate.exchange(
                "http://localhost:$port/${scenario.url}",
                scenario.httpMethod,
                HttpEntity(scenario.requestBody, scenario.httpHeaders),
                scenario.responseClass
            )
            assertResponse(responseEntity, scenario.expectedHttpStatus, scenario.expectedResponseBody)
        }
    }

    private fun assertResponse(
        responseEntity: ResponseEntity<ResponseType>,
        expectedHttpStatus: HttpStatus,
        expectedResponseBody: ResponseType
    ) {
        Assert.assertEquals(expectedHttpStatus, responseEntity.statusCode)
        Assert.assertEquals(expectedResponseBody, responseEntity.body)
//      Assertions.assertThat(responseEntity.body).usingRecursiveComparison().ignoringFields("header")
//            .isEqualTo(expectedResponseBody)
    }
}