package io.skambo.example.integration

import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import io.skambo.example.integration.rules.ClearDatabaseRule
import io.skambo.example.integration.utils.TestScenario
import org.assertj.core.api.Assertions
import org.json.JSONObject
import org.junit.Assert
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.LocalHostUriTemplateHandler
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@ActiveProfiles("memory-test")
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ApiBaseIntegrationTest<RequestType, ResponseType> {
    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    @Rule
    lateinit var clearDatabaseRule: ClearDatabaseRule

    @Autowired
    protected lateinit var context: ApplicationContext

    protected lateinit var testRestTemplate: TestRestTemplate

    @Value("#{'\${http.auth-token}'.split(',')[0]}")
    protected val testAuthorizationToken: String? = null

    @BeforeEach
    fun setUp(){
        this.testRestTemplate = TestRestTemplate()
        this.testRestTemplate.setUriTemplateHandler(LocalHostUriTemplateHandler(context.environment))
    }

    @Test
    fun integrationTest(){
        runTestScenarios(createTestScenarios())
    }

    abstract fun createTestScenarios(): List<TestScenario<RequestType, ResponseType>>

    private fun runTestScenarios(
        testScenarios: List<TestScenario<RequestType, ResponseType>>
    ){
        for(scenario: TestScenario<RequestType, ResponseType> in testScenarios){
            val responseEntity: ResponseEntity<ResponseType> = this.testRestTemplate.exchange(
                scenario.url,
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