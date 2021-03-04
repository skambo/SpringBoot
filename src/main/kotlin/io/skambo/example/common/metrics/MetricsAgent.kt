package io.skambo.example.common.metrics

import org.springframework.util.StopWatch


interface MetricsAgent {
    fun incrementCounter(name: String)
    fun incrementCounter(name: String,  metricTags: MetricTags)
    fun incrementCounter(name: String,  metricTags: MetricTags, count: Int)
    fun recordTimer(name: String, metricTags: MetricTags, stopWatch: StopWatch)
}