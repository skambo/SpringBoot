package io.skambo.example.common.metrics

import io.micrometer.core.instrument.Tags
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MetricsTagsTest {
    @BeforeEach
    fun setUp(){

    }

    @AfterEach
    fun tearDown(){

    }

    @Test
    fun testCreatingMetricTags(){
        val metricTags: MetricTags = MetricTags("tag", "value")
            .and("anotherTag", "anotherValue")
            .and("anotherTagOne", "anotherValueOne")

        val expectedTags: Tags = Tags.of("tag", "value")
            .and("anotherTag", "anotherValue")
            .and("anotherTagOne", "anotherValueOne")

        Assert.assertEquals(expectedTags, metricTags.toTags())
        Assert.assertEquals(3, metricTags.size())
    }

    @Test
    fun testNullTagKeyAndValue(){
        val metricTags: MetricTags = MetricTags("", "value")

        Assert.assertEquals(Tags.empty(), metricTags.toTags())
        Assert.assertEquals(0, metricTags.size())
    }
}