package io.skambo.example.infrastructure.exceptions

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import kotlin.Exception

@ControllerAdvice
class RestResponseEntityExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [(DuplicateUserException::class)])
    fun handleDuplicateUser(exception:DuplicateUserException, webRequest: WebRequest): ResponseEntity<Any>{
        val response = mapOf<String, String>("error" to exception.message)
        return ResponseEntity<Any>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(Exception::class)])
    fun handleInternalServerError(exception:Exception, webRequest: WebRequest): ResponseEntity<Any>{
        val response = mapOf<String, String>("error" to "An unexpected error has happened")
        return ResponseEntity<Any>(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}