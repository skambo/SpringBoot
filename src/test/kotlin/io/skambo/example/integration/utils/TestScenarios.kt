package io.skambo.example.integration.utils

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

data class TestScenario <RequestType, ResponseType> (
//    val url: String,
//    val httpMethod: HttpMethod,
    val httpHeaders: HttpHeaders,
    val requestBody: RequestType,
    val expectedHttpStatus: HttpStatus,
    val expectedResponseBody: ResponseType,
    val responseClass: Class<ResponseType>
) {
}