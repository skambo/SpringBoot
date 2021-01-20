package io.skambo.example.infrastructure.api.config

import io.skambo.example.infrastructure.api.common.interceptors.ApiRequestValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class HttpRequestInterceptorConfig: WebMvcConfigurer {
    @Autowired
    private lateinit var apiRequestValidationInterceptor: ApiRequestValidationInterceptor

    override fun addInterceptors(registry: InterceptorRegistry){
        registry.addInterceptor(apiRequestValidationInterceptor)
    }
}