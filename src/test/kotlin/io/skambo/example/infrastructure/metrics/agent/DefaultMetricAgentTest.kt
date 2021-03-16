package io.skambo.example.infrastructure.metrics.agent

import io.skambo.example.common.metrics.MetricTags
import io.skambo.example.common.metrics.MetricType
import io.skambo.example.common.metrics.MetricsAgent
import io.skambo.example.common.metrics.MetricsHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.util.StopWatch

class DefaultMetricAgentTest {
    private lateinit var metricsAgent: MetricsAgent

    @BeforeEach
    fun setUp(){
        metricsAgent = DefaultMetricsAgent()
    }

    @AfterEach
    fun tearDown(){

    }

    // TODO: find a way to assert that the metric was recorded in the global registry
    @Test
    fun testCounters(){
        val metricName: String = MetricsHelper.getMetricName(
            type = MetricType.COUNTER, component = "test", subcomponents = arrayOf("request"))
        val metricTags: MetricTags = MetricTags("tag", "value")
            .and("anotherTag", "anotherValue")
            .and("anotherTagOne", "anotherValueOne")

        metricsAgent.incrementCounter(metricName)
        metricsAgent.incrementCounter(metricName, metricTags)
        metricsAgent.incrementCounter(metricName, metricTags, 2)
    }

    @Test
    fun testTimers() {
        // TODO: find a way to assert that the metric was recorded in the global registry
        val stopWatch: StopWatch = StopWatch()
        stopWatch.start()
        val metricName: String  =  MetricsHelper.getMetricName(
            type = MetricType.TIMER,
            component = "test",
            subcomponents = arrayOf("request"))
        val metricTags: MetricTags = MetricTags("key", "value").and("key1", "value1")
        stopWatch.stop()
        metricsAgent.recordTimer(metricName, stopWatch)
        metricsAgent.recordTimer(metricName, metricTags, stopWatch)
    }
}