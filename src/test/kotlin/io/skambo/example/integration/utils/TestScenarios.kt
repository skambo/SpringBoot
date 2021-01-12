package io.skambo.example.integration.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

data class TestScenario <RequestClass, ResponseClass> (
    val url: String,
    val httpMethod: HttpMethod,
    val httpHeaders: HttpHeaders,
    val requestBody: RequestClass,
    val expectedHttpStatus: HttpStatus,
    val expectedResponseBody: ResponseClass
) {
}