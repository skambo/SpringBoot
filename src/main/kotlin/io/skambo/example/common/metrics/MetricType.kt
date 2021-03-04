package io.skambo.example.common.metrics

enum class MetricType(val value: String) {
    COUNTER("count"),
    TIMER("duration"),
    GAUGE("value"),
    ERROR("errors")
}