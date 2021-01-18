package io.skambo.example.integration

import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.greeting.v1.dto.GreetingResponse
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.util.*


@ActiveProfiles("memory-test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingIntegrationTest {
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

    @Test
    fun testGreeting(){
        val response = testRestTemplate.exchange(
            "http://localhost:$port/api/v1/greeting",
            HttpMethod.GET,
            HttpEntity<Any>(httpHeaders),
            GreetingResponse::class.java
            )
        Assert.assertEquals(HttpStatus.OK, response.statusCode)
    }
}