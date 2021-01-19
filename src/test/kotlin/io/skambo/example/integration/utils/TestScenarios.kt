package io.skambo.example.integration.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

data class TestScenario <RequestType, ResponseType> (
    val httpHeaders: HttpHeaders? = null,
    val requestBody: RequestType? = null,
    val expectedHttpStatus: HttpStatus,
    val expectedResponseBody: ResponseType,
    val responseClass: Class<ResponseType>
) {
}