package io.skambo.example.common.metrics

import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MetricsHelperTest {
    private val component: String = "web"
    private val metricType: MetricType = MetricType.COUNTER
    private val subComponent: String = "test"
    private val metricPoint: String = "request"

    @BeforeEach
    fun setUp(){

    }

    @AfterEach
    fun tearDown(){

    }

    @Test
    fun testGetMetricName(){
        val expectedMetricName: String = "eng_userservice_api.$component.$subComponent.$metricPoint.${metricType.value}"
        val metricName: String = MetricsHelper.getMetricName(
            type = metricType,
            component = component,
            subcomponents = arrayOf(subComponent, metricPoint)
        )
        Assert.assertEquals(expectedMetricName, metricName)
    }

    @Test
    fun testValidateMetricName(){
        val validNames: List<String> = listOf(
            "eng_userservice_api.$component.$subComponent.$metricPoint.${metricType.value}",
            "eng_userservice_api.$component",
            "eng_userservice_api.$component.${metricType.value}"
        )
        for (name in validNames) {
            Assert.assertTrue(MetricsHelper.validateMetricName(name))
        }
    }
}