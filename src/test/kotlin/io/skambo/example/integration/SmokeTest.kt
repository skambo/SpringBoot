package io.skambo.example.integration

import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.greeting.v1.GreetingController
import io.skambo.example.infrastructure.api.greeting.v1.dto.GreetingResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
import java.time.OffsetDateTime
import java.util.*


@ActiveProfiles(value = ["memory-test"])
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class SmokeTest {
    @Autowired
    private lateinit var greetingController: GreetingController

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
    @Throws(Exception::class)
    fun contextLoads() {
        assertThat(greetingController).isNotNull()
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