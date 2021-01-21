package io.skambo.example.integration.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

/**
 *
 */
data class TestScenario <RequestType, ResponseType>(
    val description: String,
    val endpoint: String,
    val httpHeaders: HttpHeaders? = null,
    val requestBody: RequestType? = null,
    val expectedHttpStatus: HttpStatus,
    val expectedResponseBody: ResponseType,
    val responseClass: Class<ResponseType>,
    val preScenario: () -> Unit? = {}, //This is a lambda to allow us set up data in this test scenario (or any other thing)
    val postScenario: () -> Unit? = {} // This is a lambda
) {
}