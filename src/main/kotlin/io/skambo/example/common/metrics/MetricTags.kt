package io.skambo.example.common.metrics

import io.micrometer.core.instrument.ImmutableTag
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import java.util.stream.Collectors

/**
 * A utility class that provides  a mechanism through which metric
 * tags are easily defined and applied to a metric point.
 *
 * @author kelvin.wahome
 */

class MetricTags(key: String, value: String) {
    private var map: MutableMap<String, String> = mutableMapOf()

    init {
        this.and(key, value)
    }

    /**
     * Return a new {@code MetricTags} instance by merging this collection
     * and the specified key/value pair.
     *
     * @param key the tag key to add
     * @param value the tag value to add
     * @return a new {@code MetricTags} instance
     */
    fun and(key: String, value: String): MetricTags  {
        if (key.isNotEmpty()) {
            this.map[key] = value
        }
        return this
    }

    /**
     * Return a new {@code Tags} instance by converting the cached map of
     * key-value tag pairs into a list of {@code Tags}
     *
     * @return a new {@code Tags} instance
     */
    fun toTags(): Tags {
        val tagList: List<Tag> = this.map.entries
            .stream()
            .map {entry -> ImmutableTag(entry.key, entry.value)}
            .collect(Collectors.toList())
        return Tags.of(tagList)
    }

    /**
     * Return the size of cached tags accumulated in a map
     *
     * @return Int
     */
    fun size(): Int {
        return this.map.size
    }
}

