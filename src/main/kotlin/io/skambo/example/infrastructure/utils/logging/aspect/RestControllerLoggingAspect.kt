package io.skambo.example.infrastructure.utils.logging.aspect

import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Aspect
@Component
class RestControllerLoggingAspect {
    private val LOGGER = LoggerFactory.getLogger(RestControllerLoggingAspect::class.java)

    @Pointcut(value = "within(@org.springframework.web.bind.annotation.RestController *)")
    fun restController(){
    }

    @Pointcut(value = "execution(public * *(..))")
    fun publicOperation(){
    }

    @Before(value = "restController() && publicOperation()")
    fun logBefore(joinPoint: JoinPoint){
        MDC.put("controllerName", joinPoint.signature.declaringTypeName)
        this.setRequestDetailsInLogContext(joinPoint)
        LOGGER.info("Request received")
    }

    @AfterReturning(pointcut = "restController() && publicOperation())", returning = "result")
    fun logAfter(joinPoint: JoinPoint, result: Any){
        MDC.put("response", result.toString())
        LOGGER.info("Response received")
        MDC.clear()
    }

    @AfterThrowing(pointcut = "restController()", throwing = "exception")
    fun logAfterThrowing(joinPoint: JoinPoint, exception: Throwable){
        MDC.put("EXCEPTION_MESSAGE", exception.message)
        MDC.put("STACK_TRACE", Arrays.toString(exception.stackTrace))
        LOGGER.error("An exception has occurred")
        MDC.clear()
    }

    private fun setRequestDetailsInLogContext(joinPoint: JoinPoint) {
        for (obj: Any in joinPoint.args) {
            if (obj is HttpServletRequest) {
                val servletRequest:HttpServletRequest = obj
                MDC.put("METHOD", servletRequest.method)
                MDC.put("URI", servletRequest.requestURI)
                MDC.put("REQUEST_GROUP_ID", servletRequest.getHeader(ApiHeaderKey.GROUP_ID.value))
                MDC.put("REQUEST_MESSAGE_ID", servletRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value))
                MDC.put("REQUEST_TIMESTAMP", servletRequest.getHeader(ApiHeaderKey.TIMESTAMP.value))
                break;
            }
        }
        if (joinPoint.args != null && joinPoint.args.isNotEmpty()) {
            MDC.put("REQUEST", joinPoint.args[0].toString())
        } else {
            MDC.put("REQUEST", null)
        }
    }
}