package io.skambo.example.infrastructure.metrics.agent

import io.micrometer.core.instrument.Metrics
import io.skambo.example.common.metrics.MetricTags
import io.skambo.example.common.metrics.MetricsAgent
import io.skambo.example.common.metrics.MetricsHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch
import java.util.concurrent.TimeUnit

@Component(value = "defaultMetricsAgent")
class DefaultMetricsAgent: MetricsAgent {
    private val LOGGER: Logger = LoggerFactory.getLogger(DefaultMetricsAgent::class.java)

    override fun incrementCounter(name: String) {
        try{
            MetricsHelper.validateMetricName(name)
            Metrics.counter(name).increment()
        } catch (exception:RuntimeException) {
            this.logMetricError(exception, null)
        }
    }

    override fun incrementCounter(name: String, metricTags: MetricTags) {
        try{
            MetricsHelper.validateMetricName(name)
            Metrics.counter(name, metricTags.toTags()).increment()
        } catch (exception:RuntimeException) {
            this.logMetricError(exception, metricTags)
        }
    }

    override fun incrementCounter(name: String, metricTags: MetricTags, count: Int) {
        try{
            MetricsHelper.validateMetricName(name)
            Metrics.counter(name, metricTags.toTags()).increment(count.toDouble())
        } catch (exception:RuntimeException) {
            this.logMetricError(exception, metricTags)
        }
    }

    override fun recordTimer(name: String, stopWatch: StopWatch) {
        try{
            MetricsHelper.validateMetricName(name)
            Metrics.timer(name).record(stopWatch.totalTimeMillis, TimeUnit.MILLISECONDS)
        } catch (exception:RuntimeException) {
            this.logMetricError(exception, null)
        }
    }

    override fun recordTimer(name: String, metricTags: MetricTags, stopWatch: StopWatch) {
        try{
            MetricsHelper.validateMetricName(name)
            Metrics.timer(name, metricTags.toTags()).record(stopWatch.totalTimeMillis, TimeUnit.MILLISECONDS)
        } catch (exception:RuntimeException) {
            this.logMetricError(exception, metricTags)
        }
    }

    private fun logMetricError(error: RuntimeException, metricTags: MetricTags?) {
        LOGGER.error("[metrics.unhandled.error.${error::class.simpleName}] message=${error.message} tags=$metricTags")
    }
}