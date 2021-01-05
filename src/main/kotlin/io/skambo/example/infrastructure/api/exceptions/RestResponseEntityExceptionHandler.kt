package io.skambo.example.infrastructure.api.exceptions

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.infrastructure.api.common.ErrorCodes
import io.skambo.example.infrastructure.api.common.dto.v1.ApiErrorResponse
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.format.DateTimeParseException
import javax.servlet.http.HttpServletRequest
import kotlin.Exception

@RestControllerAdvice
class RestResponseEntityExceptionHandler {
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
    fun handleUserNotFoundException(exception:UserNotFoundException, request: HttpServletRequest): ResponseEntity<ApiErrorResponse>{
        val header:Header = ApiResponseHelper.createRejectedHeader(
            httpRequest = request,
            header = null,
            errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.USER_NOT_FOUND_ERR.value),
            errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.USER_NOT_FOUND_ERR.value, exception.message.toString()))
        val response:ApiErrorResponse = ApiErrorResponse(header = header)
        return ResponseEntity<ApiErrorResponse>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(HttpRequestMethodNotSupportedException::class)])
    fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException, request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val header = ApiResponseHelper.createRejectedHeader(
            request, ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_METHOD_ERR.value),
            ApiResponseHelper.lookupErrorMessage(ErrorCodes.INVALID_METHOD_ERR.value)
        )
        val response = ApiErrorResponse(header, null)
        return ApiResponseHelper.createResponseEntity(header, response)
    }

    @ExceptionHandler(value = [(HttpMessageNotReadableException::class)])
    fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        request: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val response: ApiErrorResponse
        when(ex.rootCause) {
            is MissingKotlinParameterException -> {
                val header = ApiResponseHelper.createRejectedHeader(
                    webRequest = request,
                    errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                    errorMessage = ApiResponseHelper.lookupErrorMessage(
                        ErrorCodes.MISSING_PARAMETER_ERR_MSG.value,
                        (ex.rootCause as MissingKotlinParameterException).parameter.name.toString())
                )
                response = ApiErrorResponse(header = header, result = null)
            }
            is DateTimeParseException -> {
                val header = ApiResponseHelper.createRejectedHeader(
                    webRequest = request,
                    errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                    errorMessage = ApiResponseHelper.lookupErrorMessage(
                        ErrorCodes.INVALID_TIMESTAMP_ERR_MSG.value,
                        (ex.rootCause as DateTimeParseException).parsedString)
                )
                response = ApiErrorResponse(header = header, result = null)
            }
            else -> {
                val header = ApiResponseHelper.createRejectedHeader(
                    webRequest = request,
                    errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                    errorMessage = ex.message.toString()
                )
                response = ApiErrorResponse(header = header, result = null)
            }
        }
        return ApiResponseHelper.createResponseEntity(response.header, response)
    }

    @ExceptionHandler(value = [(NoHandlerFoundException::class)])
    fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        webRequest: WebRequest
    ): ResponseEntity<ApiErrorResponse> {
        val response = ApiErrorResponse(
            header = ApiResponseHelper.createRejectedHeader(
                webRequest,
                ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                ex.message.toString()
            )
        )
        return ApiResponseHelper.createResponseEntity(response.header, response)
    }

//    @ExceptionHandler(MalformedRequestException::class)
//    fun handleMalformedRequestException(
//        ex: MalformedRequestException,
//        request: HttpServletRequest
//    ): ResponseEntity<ApiErrorResponse> {
//        val response = ApiErrorResponse(
//            header = ApiResponseHelper.createRejectedHeader(
//                request,
//                null,
//                ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
//                ex.message
//            )
//        )
//        return ApiResponseHelper.createResponseEntity(response.header, response)
//    }

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