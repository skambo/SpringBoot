package io.skambo.example.common.metrics

enum class MetricsTagKey(val value: String) {
    COMPONENT("component"),
    OPERATION("operation"),
    SERVICE("service"),
    METRIC_POINT("metric_point")
}