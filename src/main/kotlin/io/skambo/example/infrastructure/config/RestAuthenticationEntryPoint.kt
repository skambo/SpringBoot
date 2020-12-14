package io.skambo.example.infrastructure.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.skambo.example.infrastructure.api.common.ErrorCodes
import io.skambo.example.infrastructure.api.common.dto.v1.ApiErrorResponse
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RestAuthenticationEntryPoint: AuthenticationEntryPoint {
    @Autowired
    private val objectMapper: ObjectMapper? = null

    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val header: Header = ApiResponseHelper.createRejectedHeader(
            httpRequest = request,
            header = null,
            errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_API_KEY_ERR.value),
            errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.INVALID_API_KEY_ERR.value))
        val responseBody: ApiErrorResponse = ApiErrorResponse(header = header)
        response.contentType = "application/json"
        response.status = HttpStatus.UNAUTHORIZED.value()
        val output = response.outputStream

        objectMapper!!.writeValue(output, responseBody) //!! is the null safety check
        output.flush()
    }
}