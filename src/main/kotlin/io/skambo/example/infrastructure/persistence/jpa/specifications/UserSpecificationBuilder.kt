package io.skambo.example.infrastructure.persistence.jpa.specifications

import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import org.springframework.data.jpa.domain.Specification
import java.util.*
import java.util.stream.Collectors


class UserSpecificationBuilder() {
    private var params: MutableList<FilterCriteria> = ArrayList<FilterCriteria>()

    fun with(key: String, operation: String, value: Any): UserSpecificationBuilder {
        params.add(FilterCriteria(key, operation, value))
        return this
    }

    fun build(): Specification<UserDataModel>? {
        if (params.size == 0) {
            return null
        }
        val specs: List<Specification<UserDataModel>> = params.stream()
            .map { criteria: FilterCriteria? ->
                UserSpecification(
                    criteria!!
                )
            }
            .collect(Collectors.toList())
        var result: Specification<UserDataModel>? = specs[0]
        for (i in 1 until params.size) {
            //TODO: We need to differentiate between AND and OR predicates
            result = Specification.where<UserDataModel>(result).and(specs[i] as Specification<UserDataModel>?)
        }
        return result
    }
}