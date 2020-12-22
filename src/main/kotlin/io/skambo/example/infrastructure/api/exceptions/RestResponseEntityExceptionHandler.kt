package io.skambo.example.infrastructure.api.exceptions

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.infrastructure.api.common.ErrorCodes
import io.skambo.example.infrastructure.api.common.dto.v1.ApiErrorResponse
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.servlet.http.HttpServletRequest
import kotlin.Exception

@ControllerAdvice
class RestResponseEntityExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [(DuplicateUserException::class)])
    fun handleDuplicateUserException(exception:DuplicateUserException, request: HttpServletRequest): ResponseEntity<ApiErrorResponse>{
        val header:Header = ApiResponseHelper.createRejectedHeader(
            httpRequest = request,
            header = null,
            errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.DUPLICATE_USER_ERR.value),
            errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.DUPLICATE_USER_ERR.value, exception.message.toString()))
        val response:ApiErrorResponse = ApiErrorResponse(header = header)
        return ResponseEntity<ApiErrorResponse>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(UserNotFoundException::class)])
    fun handleUserNotFoundException(exception:UserNotFoundException, webRequest: WebRequest): ResponseEntity<Any>{
        val response = mapOf<String, String>("error" to exception.message)
        return ResponseEntity<Any>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(Exception::class)])
    fun handleInternalServerError(exception:Exception, request: HttpServletRequest): ResponseEntity<ApiErrorResponse>{
        val header:Header = ApiResponseHelper.createFailureHeader(
            httpRequest = request,
            header = null,
            errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.UNKNOWN_FAILURE_ERR.value),
            errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.UNKNOWN_FAILURE_ERR.value, exception.message.toString()))
        val response:ApiErrorResponse = ApiErrorResponse(header = header)
        return ApiResponseHelper.createResponseEntity(header, response)
    }
}