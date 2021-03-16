package io.skambo.example

import io.micrometer.core.instrument.MeterRegistry
//import io.micrometer.spring.autoconfigure.MeterRegistryCustomizer
import io.skambo.example.common.metrics.MetricsTagKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication(scanBasePackages = ["io.skambo.example"])
class SpringExampleApplication{
    @Value("\${application.config.service.name}")
    private val serviceName: String? = null

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<SpringExampleApplication>(*args)
        }
    }

    //TODO Fix the micrometer-datadog integration
//    @Bean
//    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
//        return MeterRegistryCustomizer<MeterRegistry> { registry ->
//            registry.config().commonTags(MetricsTagKey.SERVICE.value, serviceName)
//        }
//    }
}


