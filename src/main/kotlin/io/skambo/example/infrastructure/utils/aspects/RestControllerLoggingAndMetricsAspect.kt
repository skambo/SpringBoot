package io.skambo.example.infrastructure.utils.aspects

import io.skambo.example.common.metrics.*
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Aspect
@Component
class RestControllerLoggingAndMetricsAspect {
    private val LOGGER = LoggerFactory.getLogger(RestControllerLoggingAndMetricsAspect::class.java)

    @Autowired
    private lateinit var metricsAgent: MetricsAgent

    @Pointcut(value = "within(@org.springframework.web.bind.annotation.RestController *)")
    fun restController(){
    }

    @Pointcut(value = "within(@org.springframework.web.bind.annotation.RestControllerAdvice *)")
    fun restControllerAdvice(){
    }

    @Pointcut(value = "execution(public * *(..))")
    fun publicOperation(){
    }

    @Before(value = "restController() && publicOperation()")
    fun beforeHandlingRequest(joinPoint: JoinPoint){
        MDC.put("controllerName", joinPoint.signature.declaringTypeName)
        this.setRequestDetailsInLogContext(joinPoint)
        LOGGER.info("Request received")

        metricsAgent.incrementCounter(
            name = MetricsHelper.getMetricName(
                type = MetricType.COUNTER,
                component = MetricsWayPoint.webComponent,
                subcomponents = arrayOf(joinPoint.signature.name, MetricsWayPoint.requestMetricPoint)
            ),
            metricTags = MetricTags(MetricsTagKey.COMPONENT.value, MetricsWayPoint.webComponent)
                .and(MetricsTagKey.OPERATION.value, joinPoint.signature.name)
                .and(MetricsTagKey.METRIC_POINT.value, MetricsWayPoint.requestMetricPoint)
        )
    }

    @AfterReturning(pointcut = "restController() || restControllerAdvice() && publicOperation())", returning = "result")
    fun afterReturningResponse(joinPoint: JoinPoint, result: Any){
        MDC.put("response", result.toString())
        LOGGER.info("Response received")
        MDC.clear()
    }

    @AfterThrowing(pointcut = "restController()", throwing = "exception")
    fun afterAnExceptionIsThrown(joinPoint: JoinPoint, exception: Throwable){
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