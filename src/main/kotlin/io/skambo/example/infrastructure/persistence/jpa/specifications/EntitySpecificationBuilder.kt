package io.skambo.example.infrastructure.persistence.jpa.specifications

import org.springframework.data.jpa.domain.Specification
import java.util.*
import java.util.stream.Collectors


class EntitySpecificationBuilder<Entity>() {
    private var params: MutableList<FilterCriteria> = ArrayList<FilterCriteria>()

    fun with(key: String, operation: String, value: Any): EntitySpecificationBuilder<Entity> {
        params.add(FilterCriteria(key, operation, value))
        return this
    }

    fun build(): Specification<Entity>? {
        if (params.size == 0) {
            return null
        }
        val specs: List<Specification<Entity>> = params.stream()
            .map { criteria: FilterCriteria? -> EntitySpecification<Entity>(criteria!!) }
            .collect(Collectors.toList())
        var result: Specification<Entity>? = specs[0]
        for (i in 1 until params.size) {
            //TODO: We need to differentiate between AND and OR predicates
            result = Specification.where<Entity>(result)?.and(specs[i] as Specification<Entity>?)
        }
        return result
    }
}