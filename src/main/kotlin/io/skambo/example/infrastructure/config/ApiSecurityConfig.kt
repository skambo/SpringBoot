package io.skambo.example.infrastructure.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import javax.servlet.http.HttpServletRequest

@Configuration
@EnableWebSecurity
open class ApiSecurityConfig: WebSecurityConfigurerAdapter() {
    @Value("\${http.endpoint-pattern}")
    private val antPattern: String? = null

    @Value("\${http.auth-token-header-name}")
    private val principalRequestHeader: String? = null

    @Value("#{'\${http.auth-token}'.split(',')}")
    private val principalRequestTokens:List<String>? = null

    @Autowired
    private val restAuthenticationEntryPoint: RestAuthenticationEntryPoint? = null

    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity.cors()
        httpSecurity.antMatcher(antPattern).csrf().disable().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().addFilter(apiAuthenticationFilter())
            .authorizeRequests().anyRequest().authenticated().and().exceptionHandling()
            .authenticationEntryPoint(restAuthenticationEntryPoint)
    }

    private fun apiAuthenticationFilter(): ApiKeyAuthFilter? {
        val filter = ApiKeyAuthFilter(principalRequestHeader)
        filter.setAuthenticationManager { authentication ->
            val principal = authentication.principal as String
            val validPrincipal = principalRequestTokens!!.stream().anyMatch {
                    principalRequestValue -> principalRequestValue.trim { it <= ' ' } == principal }
            if (!validPrincipal) {
                //not made a constant as this value gets converted by to a message that adheres to API standards
                // in the RestAuthenticationEntryPoint below.
                throw BadCredentialsException("Invalid API Key received.")
            }
            authentication.isAuthenticated = true
            authentication
        }
        return filter
    }

    inner class ApiKeyAuthFilter(private val principalRequestHeader: String?) :
        AbstractPreAuthenticatedProcessingFilter() {

        override fun getPreAuthenticatedPrincipal(request: HttpServletRequest): Any {
            return request.getHeader(principalRequestHeader) ?: return ""
        }

        override fun getPreAuthenticatedCredentials(request: HttpServletRequest): Any {
            return "N/A"
        }
    }

}