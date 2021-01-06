package io.skambo.example.infrastructure.api.common.filters

import io.skambo.example.infrastructure.api.common.helpers.ApiRequestValidationHelper
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ApiRequestValidationInterceptor: HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response:HttpServletResponse, handler:Any): Boolean {
        // if(request.method in listOf(HttpMethod.POST.toString(), HttpMethod.PUT.toString(), HttpMethod.PATCH.toString())) {
        // }
        ApiRequestValidationHelper.validateMandatoryRequestHeaders(request)
        return true
    }
}