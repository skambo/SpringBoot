package io.skambo.example.integration

import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import io.skambo.example.integration.rules.ClearDatabaseRule
import io.skambo.example.integration.utils.TestScenario
import org.json.JSONObject
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
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
abstract class BaseIntegrationTest {
    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    @Rule
    protected lateinit var clearDatabaseRule: ClearDatabaseRule

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

    abstract fun <RequestClass, ResponseClass> createTestScenarios(): List<TestScenario<RequestClass, ResponseClass>>

    private inline fun <RequestClass, reified ResponseClass> runTestScenarios(
        testScenarios: List<TestScenario<RequestClass, ResponseClass>>
    ){
        for(scenario: TestScenario<RequestClass, ResponseClass> in testScenarios){
            val expectedHttpStatus: HttpStatus = scenario.expectedHttpStatus
            val expectedResponseBody: ResponseClass = scenario.expectedResponseBody

            val responseEntity: ResponseEntity<ResponseClass> = this.testRestTemplate.exchange(
                scenario.url,
                scenario.httpMethod,
                HttpEntity(scenario.requestBody, scenario.httpHeaders),
                ResponseClass::class.java
            )
        }
    }
}