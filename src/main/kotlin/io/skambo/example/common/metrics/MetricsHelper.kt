package io.skambo.example.common.metrics

import io.skambo.example.common.metrics.exceptions.InvalidMetricNameException

object MetricsHelper {
    private val SEPARATOR_TOKEN: String = "."
    private val SERVICE_NAME: String = "eng_userservice_api"

    fun getMetricName(type: MetricType, component: String , subcomponents: Array<String>): String {
        var components: String = component
        if (subcomponents.isNotEmpty()) {
            val joinedSubComponents: String = subcomponents.joinToString(separator = SEPARATOR_TOKEN)
            components = "$component$SEPARATOR_TOKEN$joinedSubComponents"
        }
        return "$SERVICE_NAME$SEPARATOR_TOKEN$components$SEPARATOR_TOKEN${type.value}"
    }

    @Throws(InvalidMetricNameException::class)
    fun validateMetricName(metricName: String ): Boolean {
        // . needs to be escaped when used as delimiter in split()
        val splitName: List<String> = metricName.split(String.format("\\%s", SEPARATOR_TOKEN))

        if (!((splitName[0] == SERVICE_NAME) && splitName.size >= 2)) {
            throw InvalidMetricNameException(
                "The name='$metricName' is not a valid metric name. It does not conform to the published standards")
        }
        return true
    }
}